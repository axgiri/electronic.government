package github.axgiri.AuthenticationService.hadnlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionsAdvice {

    @ExceptionHandler({
        Exception.class
    })
    public ErrorResponse error(final Exception error) {
        return new ErrorResponse(HttpStatus.PAYMENT_REQUIRED, error.getMessage());
    }

    public record ErrorResponse (
         HttpStatus status,
         String message
    ) {}
}
