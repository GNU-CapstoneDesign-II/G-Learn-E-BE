package gnu.capstone.G_Learn_E.global.error;

import gnu.capstone.G_Learn_E.global.error.dto.ErrorResponse;
import gnu.capstone.G_Learn_E.global.error.exception.client.AccessDeniedGroupException;
import gnu.capstone.G_Learn_E.global.error.exception.client.AuthGroupException;
import gnu.capstone.G_Learn_E.global.error.exception.client.InvalidGroupException;
import gnu.capstone.G_Learn_E.global.error.exception.client.NotFoundGroupException;
import gnu.capstone.G_Learn_E.global.error.exception.server.InternalServerErrorGroupException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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




    // 400, 파라미터 유효성 오류
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) {

        FieldError fieldError = e.getBindingResult().getFieldErrors().getFirst();
        String msg = fieldError.getDefaultMessage();
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), msg);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    /**
     * 1) 필수 파라미터가 누락되었을 때(MissingServletRequestParameterException)
     * 예: @RequestParam으로 받는 name 파라미터가 아예 없을 경우
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        // 파라미터 이름
        String paramName = ex.getParameterName();
        String message = paramName + "은(는) 필수 파라미터입니다.";
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 2) 타입이 맞지 않아 생기는 에러(TypeMismatchException, MethodArgumentTypeMismatchException)
     * 예: double 파라미터 자리에 문자열이 들어온 경우
     */
    @ExceptionHandler({TypeMismatchException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(Exception ex) {
        String message = "파라미터의 타입이 올바르지 않습니다.";
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 공통 에러 응답 생성
    private ResponseEntity<ErrorResponse> createErrorResponse(RuntimeException e, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(status.value(), e.getMessage());
        log.error("Error [{}]: {}", status.value(), e.getMessage());

        return new ResponseEntity<>(errorResponse, status);
    }
}
