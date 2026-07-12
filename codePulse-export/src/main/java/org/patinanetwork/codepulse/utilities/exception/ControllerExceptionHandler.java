package org.patinanetwork.codepulse.utilities.exception;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.patinanetwork.codepulse.common.dto.ApiResponder;
import org.patinanetwork.codepulse.common.env.Env;
import org.patinanetwork.codepulse.common.reporter.Reporter;
import org.patinanetwork.codepulse.common.reporter.report.Report;
import org.patinanetwork.codepulse.common.reporter.report.location.Location;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    private final Reporter errorReporter;
    private final Env env;

    public ControllerExceptionHandler(final Reporter errorReporter, final Env env) {
        this.errorReporter = errorReporter;
        this.env = env;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponder<?>> handleResponseStatusException(final ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ApiResponder.failure(ex.getReason()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponder<?>> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex) {
        var errors =
                ex.getFieldErrors().stream().map(e -> e.getDefaultMessage()).toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponder.failure(String.join(", ", errors)));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponder<?>> handleMethodArgumentNotValid(final ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponder.failure(ex.getMessage()));
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ApiResponder<?>> handleCallNotPermitted(final CallNotPermittedException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponder.failure("The code execution service is temporarily unavailable. Please try again in a moment."));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiResponder<?>> handleThrowable(final Throwable rx) {
        log.error(rx.getMessage(), rx);

        if (ExcludedExceptions.isValid(rx)) {
            errorReporter.error(
                    "handleThrowable",
                    Report.builder()
                            .environments(env.getActiveProfiles())
                            .location(Location.BACKEND)
                            .data(Reporter.throwableToString(rx))
                            .build());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponder.failure(rx.getMessage()));
    }
}
