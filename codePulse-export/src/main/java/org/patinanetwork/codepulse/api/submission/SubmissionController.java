package org.patinanetwork.codepulse.api.submission;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import org.patinanetwork.codepulse.common.db.models.question.QuestionWithUser;
import org.patinanetwork.codepulse.common.db.repos.question.QuestionRepository;
import org.patinanetwork.codepulse.common.dto.ApiResponder;
import org.patinanetwork.codepulse.common.dto.question.QuestionWithUserDto;
import org.patinanetwork.codepulse.common.security.AuthenticationObject;
import org.patinanetwork.codepulse.common.security.annotation.Protected;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves details of a single accepted-submission ({@code Question}) row for the submission-details
 * view page. This is distinct from the removed LeetCode submission-polling/username-linking
 * pipeline (old {@code SubmissionController}, deleted) — {@code Question} rows themselves are still
 * live (populated by the internal judge now, previously by LeetCode polling), this controller just
 * exposes reading one by id. Mounted under {@code /api/leetcode/submission} to match the existing
 * frontend/schema path rather than introduce a rename.
 */
@RestController
@Tag(name = "Submission routes", description = "Routes for viewing details of a single past submission.")
@RequestMapping("/api/leetcode/submission")
@Timed(value = "controller.execution")
public class SubmissionController {

    private final QuestionRepository questionRepository;

    public SubmissionController(final QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Operation(
            summary = "Returns submission data.",
            description = "Returns the submission data from any user, as long as the user making the "
                    + "request is authenticated.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful response (check success key)"),
            })
    @GetMapping("/{submissionId}")
    public ResponseEntity<ApiResponder<QuestionWithUserDto>> getSubmissionBySubmissionId(
            @PathVariable("submissionId") final String submissionId,
            @Protected final AuthenticationObject authenticationObject) {
        Optional<QuestionWithUser> question = questionRepository.getQuestionWithUserById(submissionId);

        if (question.isEmpty()) {
            return ResponseEntity.ok(ApiResponder.failure("Submission not found."));
        }

        return ResponseEntity.ok(ApiResponder.success(
                "Submission found!", QuestionWithUserDto.fromQuestionWithUser(question.get())));
    }
}
