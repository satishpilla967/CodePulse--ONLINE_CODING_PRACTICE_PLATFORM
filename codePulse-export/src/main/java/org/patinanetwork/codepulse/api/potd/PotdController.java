package org.patinanetwork.codepulse.api.potd;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import org.patinanetwork.codepulse.common.db.models.potd.POTD;
import org.patinanetwork.codepulse.common.db.repos.potd.POTDRepository;
import org.patinanetwork.codepulse.common.dto.ApiResponder;
import org.patinanetwork.codepulse.common.dto.potd.PotdDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves the current "Problem of the Day" (sourced from LeetCode's own POTD via {@code
 * LeetcodePotdClient}/{@code PotdSetter}, an intentionally-retained capability separate from the
 * removed LeetCode submission-polling pipeline). Mounted under {@code /api/leetcode/potd} to match
 * the existing frontend/schema paths rather than introduce a rename across three frontend files.
 */
@RestController
@Tag(name = "POTD routes", description = "Routes for fetching the current Problem of the Day.")
@RequestMapping("/api/leetcode/potd")
@Timed(value = "controller.execution")
public class PotdController {

    private final POTDRepository potdRepository;

    public PotdController(final POTDRepository potdRepository) {
        this.potdRepository = potdRepository;
    }

    @Operation(
            summary = "Returns current problem of the day.",
            description =
                    "Returns the current problem of the day, as long as one has been set for today.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful response (check success key)"),
            })
    @GetMapping("")
    public ResponseEntity<ApiResponder<PotdDto>> getCurrentPotd() {
        Optional<POTD> potd = potdRepository.getCurrentPOTD();

        if (potd.isEmpty()) {
            return ResponseEntity.ok(ApiResponder.failure("No problem of the day has been set yet."));
        }

        return ResponseEntity.ok(ApiResponder.success("POTD found!", PotdDto.fromPOTD(potd.get())));
    }

    @Operation(
            summary = "Returns current problem of the day for embedding.",
            description = "Returns the current problem of the day for use in embeds, as long as there is a "
                    + "problem of the day set for today. This endpoint does not require user authentication and "
                    + "does not check if the user has already completed the problem.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful response (check success key)"),
            })
    @GetMapping("/embed")
    public ResponseEntity<ApiResponder<PotdDto>> getCurrentPotdEmbed() {
        return getCurrentPotd();
    }
}
