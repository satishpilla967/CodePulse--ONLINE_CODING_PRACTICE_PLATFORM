package org.patinanetwork.codepulse.api.admin;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.patinanetwork.codepulse.api.admin.body.judge.CreateBuggyCodeBody;
import org.patinanetwork.codepulse.api.admin.body.judge.CreateProblemBody;
import org.patinanetwork.codepulse.api.admin.body.judge.CreateStarterCodeBody;
import org.patinanetwork.codepulse.api.admin.body.judge.CreateTestCaseBody;
import org.patinanetwork.codepulse.common.db.models.judge.Problem;
import org.patinanetwork.codepulse.common.db.models.judge.ProblemBuggyCode;
import org.patinanetwork.codepulse.common.db.models.judge.ProblemStarterCode;
import org.patinanetwork.codepulse.common.db.models.judge.TestCase;
import org.patinanetwork.codepulse.common.db.repos.judge.ProblemBuggyCodeRepository;
import org.patinanetwork.codepulse.common.db.repos.judge.ProblemRepository;
import org.patinanetwork.codepulse.common.db.repos.judge.ProblemStarterCodeRepository;
import org.patinanetwork.codepulse.common.db.repos.judge.TestCaseRepository;
import org.patinanetwork.codepulse.common.dto.ApiResponder;
import org.patinanetwork.codepulse.common.dto.Empty;
import org.patinanetwork.codepulse.common.dto.judge.ProblemAdminDto;
import org.patinanetwork.codepulse.common.dto.judge.ProblemDto;
import org.patinanetwork.codepulse.common.security.AuthenticationObject;
import org.patinanetwork.codepulse.common.security.annotation.Protected;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Admin-authored problem/test-case/starter-code CRUD, mirroring {@link AdminController}'s announcement CRUD pattern. */
@RestController
@Tag(name = "Admin judge routes", description = "Admin CRUD for judge problems, test cases, and starter code.")
@RequestMapping("/api/admin/judge")
@Timed(value = "controller.execution")
public class JudgeAdminController {

    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;
    private final ProblemStarterCodeRepository problemStarterCodeRepository;
    private final ProblemBuggyCodeRepository problemBuggyCodeRepository;

    public JudgeAdminController(
            final ProblemRepository problemRepository,
            final TestCaseRepository testCaseRepository,
            final ProblemStarterCodeRepository problemStarterCodeRepository,
            final ProblemBuggyCodeRepository problemBuggyCodeRepository) {
        this.problemRepository = problemRepository;
        this.testCaseRepository = testCaseRepository;
        this.problemStarterCodeRepository = problemStarterCodeRepository;
        this.problemBuggyCodeRepository = problemBuggyCodeRepository;
    }

    @Operation(summary = "List all judge problems.")
    @GetMapping("/problem")
    public ResponseEntity<ApiResponder<List<ProblemDto>>> getAllProblems(
            @Protected(admin = true) final AuthenticationObject authenticationObject) {
        List<ProblemDto> problems =
                problemRepository.getAllProblems().stream().map(ProblemDto::fromProblem).toList();
        return ResponseEntity.ok(ApiResponder.success("Problems found.", problems));
    }

    @Operation(summary = "Get a single problem by id, including hidden test cases and all starter code (admin-only).")
    @GetMapping("/problem/{id}")
    public ResponseEntity<ApiResponder<ProblemAdminDto>> getProblem(
            @PathVariable("id") final String id, @Protected(admin = true) final AuthenticationObject authenticationObject) {
        Problem problem = problemRepository.getProblemById(id);
        if (problem == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found.");
        }
        List<TestCase> testCases = testCaseRepository.getTestCasesByProblemId(id);
        List<ProblemStarterCode> starterCode = problemStarterCodeRepository.getStarterCodeByProblemId(id);
        List<ProblemBuggyCode> buggyCode = problemBuggyCodeRepository.getBuggyCodeByProblemId(id);
        return ResponseEntity.ok(ApiResponder.success(
                "Problem found.", ProblemAdminDto.fromProblem(problem, testCases, starterCode, buggyCode)));
    }

    @Operation(summary = "Create a new judge problem.")
    @PostMapping("/problem/create")
    public ResponseEntity<ApiResponder<ProblemDto>> createProblem(
            @Valid @RequestBody final CreateProblemBody body,
            @Protected(admin = true) final AuthenticationObject authenticationObject) {
        Problem problem = Problem.builder()
                .title(body.getTitle())
                .slug(body.getSlug())
                .difficulty(body.getDifficulty())
                .category(body.getCategory() != null ? body.getCategory() : org.patinanetwork.codepulse.common.db.models.judge.ProblemCategory.DSA)
                .statement(body.getStatement())
                .constraints(body.getConstraints())
                .timeLimitMs(body.getTimeLimitMs() != null ? body.getTimeLimitMs() : 2000)
                .memoryLimitKb(body.getMemoryLimitKb() != null ? body.getMemoryLimitKb() : 128000)
                .createdBy(authenticationObject.getUser().getId())
                .build();
        Problem created = problemRepository.createProblem(problem);
        return ResponseEntity.ok(ApiResponder.success("Problem created.", ProblemDto.fromProblem(created)));
    }

    @Operation(summary = "Delete a judge problem (cascades test cases/starter code/submissions).")
    @DeleteMapping("/problem/{id}")
    public ResponseEntity<ApiResponder<Empty>> deleteProblem(
            @PathVariable("id") final String id, @Protected(admin = true) final AuthenticationObject authenticationObject) {
        problemRepository.deleteProblem(id);
        return ResponseEntity.ok(ApiResponder.success("Problem deleted.", Empty.of()));
    }

    @Operation(summary = "Add a test case to a problem.")
    @PostMapping("/test-case/create")
    public ResponseEntity<ApiResponder<Empty>> createTestCase(
            @Valid @RequestBody final CreateTestCaseBody body,
            @Protected(admin = true) final AuthenticationObject authenticationObject) {
        TestCase testCase = TestCase.builder()
                .problemId(body.getProblemId())
                .input(body.getInput())
                .expectedOutput(body.getExpectedOutput())
                .isHidden(body.isHidden())
                .displayOrder(body.getDisplayOrder())
                .build();
        testCaseRepository.createTestCase(testCase);
        return ResponseEntity.ok(ApiResponder.success("Test case created.", Empty.of()));
    }

    @Operation(summary = "Delete a test case.")
    @DeleteMapping("/test-case/{id}")
    public ResponseEntity<ApiResponder<Empty>> deleteTestCase(
            @PathVariable("id") final String id, @Protected(admin = true) final AuthenticationObject authenticationObject) {
        testCaseRepository.deleteTestCase(id);
        return ResponseEntity.ok(ApiResponder.success("Test case deleted.", Empty.of()));
    }

    @Operation(summary = "Add per-language starter code to a problem.")
    @PostMapping("/starter-code/create")
    public ResponseEntity<ApiResponder<Empty>> createStarterCode(
            @Valid @RequestBody final CreateStarterCodeBody body,
            @Protected(admin = true) final AuthenticationObject authenticationObject) {
        ProblemStarterCode starterCode = ProblemStarterCode.builder()
                .problemId(body.getProblemId())
                .language(body.getLanguage())
                .starterCode(body.getStarterCode())
                .build();
        problemStarterCodeRepository.createStarterCode(starterCode);
        return ResponseEntity.ok(ApiResponder.success("Starter code created.", Empty.of()));
    }

    @Operation(summary = "Add a per-language buggy-code challenge to a problem (Debug Challenge mode).")
    @PostMapping("/buggy-code/create")
    public ResponseEntity<ApiResponder<Empty>> createBuggyCode(
            @Valid @RequestBody final CreateBuggyCodeBody body,
            @Protected(admin = true) final AuthenticationObject authenticationObject) {
        ProblemBuggyCode buggyCode = ProblemBuggyCode.builder()
                .problemId(body.getProblemId())
                .language(body.getLanguage())
                .buggyCode(body.getBuggyCode())
                .build();
        problemBuggyCodeRepository.createBuggyCode(buggyCode);
        return ResponseEntity.ok(ApiResponder.success("Buggy code created.", Empty.of()));
    }
}
