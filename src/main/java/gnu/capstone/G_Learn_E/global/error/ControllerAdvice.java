package gnu.capstone.G_Learn_E.global.error;

import gnu.capstone.G_Learn_E.global.error.dto.ErrorResponse;
import gnu.capstone.G_Learn_E.global.error.exception.client.AccessDeniedGroupException;
import gnu.capstone.G_Learn_E.global.error.exception.client.AuthGroupException;
import gnu.capstone.G_Learn_E.global.error.exception.client.InvalidGroupException;
import gnu.capstone.G_Learn_E.global.error.exception.client.NotFoundGroupException;
import gnu.capstone.G_Learn_E.global.error.exception.server.InternalServerErrorGroupException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    // 400, InvalidGroupException
    @ExceptionHandler({InvalidGroupException.class})
    public ResponseEntity<ErrorResponse> handleInvalidData(RuntimeException e) {
        return createErrorResponse(e, HttpStatus.BAD_REQUEST);
    }

    // 401, AuthGroupException
    @ExceptionHandler({AuthGroupException.class})
    public ResponseEntity<ErrorResponse> handleAuthDate(RuntimeException e) {
        return createErrorResponse(e, HttpStatus.UNAUTHORIZED);
    }

    // 403, AccessDeniedGroupException
    @ExceptionHandler({AccessDeniedGroupException.class})
    public ResponseEntity<ErrorResponse> handleAccessDeniedDate(RuntimeException e) {
        return createErrorResponse(e, HttpStatus.FORBIDDEN);
    }

    // 404, NotFoundGroupException
    @ExceptionHandler({NotFoundGroupException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundDate(RuntimeException e) {
        return createErrorResponse(e, HttpStatus.NOT_FOUND);
    }

    // 408, REQUEST_TIMEOUT

    // 409, ConflictGroupException


    // 418, TeapotGroupException


    // 422, UnprocessableGroupException


    // 429, ManyRequestsGroupException


    // 500, InternalServerError
    @ExceptionHandler({InternalServerErrorGroupException.class})
    public ResponseEntity<ErrorResponse> handleInternalServerDate(RuntimeException e) {
        return createErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // 502, BadGatewayGroupException




    // 메서드 인자 문제 생겼을 때
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) {
        FieldError fieldError = Objects.requireNonNull(e.getFieldError());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                String.format("%s. (%s)", fieldError.getDefaultMessage(), fieldError.getField()));
        log.warn("Validation error for field {}: {}", fieldError.getField(), fieldError.getDefaultMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 공통 에러 응답 생성
    private ResponseEntity<ErrorResponse> createErrorResponse(RuntimeException e, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(status.value(), e.getMessage());
        log.error("Error [{}]: {}", status.value(), e.getMessage());

        return new ResponseEntity<>(errorResponse, status);
    }
}
