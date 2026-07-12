package org.patinanetwork.codepulse.api.judge;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.patinanetwork.codepulse.api.judge.body.RunCodeBody;
import org.patinanetwork.codepulse.api.judge.body.SubmitSolutionBody;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeLanguage;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeSubmission;
import org.patinanetwork.codepulse.common.db.models.judge.Problem;
import org.patinanetwork.codepulse.common.db.repos.judge.JudgeSubmissionRepository;
import org.patinanetwork.codepulse.common.db.repos.judge.ProblemBuggyCodeRepository;
import org.patinanetwork.codepulse.common.db.repos.judge.ProblemRepository;
import org.patinanetwork.codepulse.common.db.repos.judge.ProblemStarterCodeRepository;
import org.patinanetwork.codepulse.common.dto.ApiResponder;
import org.patinanetwork.codepulse.common.dto.judge.JudgeSubmissionDto;
import org.patinanetwork.codepulse.common.dto.judge.ProblemBuggyCodeDto;
import org.patinanetwork.codepulse.common.dto.judge.ProblemDto;
import org.patinanetwork.codepulse.common.dto.judge.ProblemStarterCodeDto;
import org.patinanetwork.codepulse.common.dto.judge.RunCodeResultDto;
import org.patinanetwork.codepulse.common.judge.Judge0Service;
import org.patinanetwork.codepulse.common.judge0.Judge0Submission;
import org.patinanetwork.codepulse.common.security.AuthenticationObject;
import org.patinanetwork.codepulse.common.security.annotation.Protected;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Endpoints for the built-in Judge0-backed online judge: problem viewing, Run Code, Submit Solution, and polling. */
@RestController
@Tag(name = "Judge routes", description = "Endpoints for the in-app online judge (problems, run/submit code).")
@RequestMapping("/api/judge")
@Timed(value = "controller.execution")
public class JudgeController {

    private final ProblemRepository problemRepository;
    private final ProblemStarterCodeRepository problemStarterCodeRepository;
    private final ProblemBuggyCodeRepository problemBuggyCodeRepository;
    private final JudgeSubmissionRepository judgeSubmissionRepository;
    private final Judge0Service judge0Service;

    public JudgeController(
            final ProblemRepository problemRepository,
            final ProblemStarterCodeRepository problemStarterCodeRepository,
            final ProblemBuggyCodeRepository problemBuggyCodeRepository,
            final JudgeSubmissionRepository judgeSubmissionRepository,
            final Judge0Service judge0Service) {
        this.problemRepository = problemRepository;
        this.problemStarterCodeRepository = problemStarterCodeRepository;
        this.problemBuggyCodeRepository = problemBuggyCodeRepository;
        this.judgeSubmissionRepository = judgeSubmissionRepository;
        this.judge0Service = judge0Service;
    }

    private Problem requireProblem(final String problemId) {
        Problem problem = problemRepository.getProblemById(problemId);
        if (problem == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found.");
        }
        return problem;
    }

    @Operation(summary = "List all judge problems (title/slug/difficulty only, for the problem list page).")
    @GetMapping("/problem")
    public ResponseEntity<ApiResponder<List<ProblemDto>>> getAllProblems() {
        List<ProblemDto> problems =
                problemRepository.getAllProblems().stream().map(ProblemDto::fromProblem).toList();
        return ResponseEntity.ok(ApiResponder.success("Problems found.", problems));
    }

    @Operation(summary = "Get a single problem by id, including public test cases.")
    @GetMapping("/problem/{id}")
    public ResponseEntity<ApiResponder<ProblemDto>> getProblem(@PathVariable("id") final String id) {
        Problem problem = requireProblem(id);
        return ResponseEntity.ok(ApiResponder.success("Problem found.", ProblemDto.fromProblem(problem)));
    }

    @Operation(summary = "Get the starter code for a problem in a given language.")
    @GetMapping("/problem/{id}/starter-code/{language}")
    public ResponseEntity<ApiResponder<ProblemStarterCodeDto>> getStarterCode(
            @PathVariable("id") final String id, @PathVariable("language") final JudgeLanguage language) {
        var starterCode = problemStarterCodeRepository.getStarterCodeByProblemIdAndLanguage(id, language);
        if (starterCode == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No starter code for this language.");
        }
        return ResponseEntity.ok(
                ApiResponder.success("Starter code found.", ProblemStarterCodeDto.fromProblemStarterCode(starterCode)));
    }

    @Operation(summary = "List problems that have a Debug Challenge (buggy-code) available.")
    @GetMapping("/debug-challenges")
    public ResponseEntity<ApiResponder<List<ProblemDto>>> getDebugChallenges() {
        List<ProblemDto> problems = problemBuggyCodeRepository.getProblemsWithBuggyCode().stream()
                .peek(problem -> problem.setTestCases(new java.util.ArrayList<>()))
                .map(ProblemDto::fromProblem)
                .toList();
        return ResponseEntity.ok(ApiResponder.success("Debug challenges found.", problems));
    }

    @Operation(summary = "Get the Debug Challenge (buggy) code for a problem in a given language.")
    @GetMapping("/problem/{id}/buggy-code/{language}")
    public ResponseEntity<ApiResponder<ProblemBuggyCodeDto>> getBuggyCode(
            @PathVariable("id") final String id, @PathVariable("language") final JudgeLanguage language) {
        var buggyCode = problemBuggyCodeRepository.getBuggyCodeByProblemIdAndLanguage(id, language);
        if (buggyCode == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No debug challenge for this language.");
        }
        return ResponseEntity.ok(
                ApiResponder.success("Buggy code found.", ProblemBuggyCodeDto.fromProblemBuggyCode(buggyCode)));
    }

    @Operation(summary = "Run code against a custom input or the problem's public sample, without persisting or scoring.")
    @PostMapping("/run")
    public ResponseEntity<ApiResponder<RunCodeResultDto>> runCode(
            @Valid @RequestBody final RunCodeBody body,
            @Protected final AuthenticationObject authenticationObject) {
        Problem problem = requireProblem(body.getProblemId());
        Judge0Submission result =
                judge0Service.runCode(problem, body.getLanguage(), body.getSourceCode(), body.getCustomInput());
        return ResponseEntity.ok(ApiResponder.success("Code executed.", RunCodeResultDto.fromJudge0Submission(result)));
    }

    @Operation(summary = "Submit a solution against all (public + hidden) test cases; scoring happens asynchronously.")
    @PostMapping("/submit")
    public ResponseEntity<ApiResponder<JudgeSubmissionDto>> submitSolution(
            @Valid @RequestBody final SubmitSolutionBody body,
            @Protected final AuthenticationObject authenticationObject) {
        Problem problem = requireProblem(body.getProblemId());
        JudgeSubmission submission = judge0Service.submitSolution(
                authenticationObject.getUser(),
                problem,
                body.getLanguage(),
                body.getSourceCode(),
                Optional.ofNullable(body.getLobbyId()));
        return ResponseEntity.ok(ApiResponder.success("Submission received.", JudgeSubmissionDto.fromJudgeSubmission(submission)));
    }

    @Operation(summary = "Poll the status of a previously created submission.")
    @GetMapping("/submission/{id}")
    public ResponseEntity<ApiResponder<JudgeSubmissionDto>> getSubmission(
            @PathVariable("id") final String id, @Protected final AuthenticationObject authenticationObject) {
        JudgeSubmission submission = judgeSubmissionRepository.getSubmissionById(id);
        if (submission == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found.");
        }
        if (!submission.getUserId().equals(authenticationObject.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this submission.");
        }
        List<org.patinanetwork.codepulse.common.db.models.judge.JudgeSubmissionResult> results =
                judge0Service.getResultsForSubmission(id);
        submission.setResults(results);
        return ResponseEntity.ok(ApiResponder.success("Submission found.", JudgeSubmissionDto.fromJudgeSubmission(submission)));
    }
}
