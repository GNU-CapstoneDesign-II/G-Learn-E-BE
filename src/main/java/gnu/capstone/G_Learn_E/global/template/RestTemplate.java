package gnu.capstone.G_Learn_E.global.template;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@Getter
@JsonPropertyOrder({"code","message","data"})
public class RestTemplate<T> {
    int code;
    String message;
    T data;

    // 성공하였을 경우
    public RestTemplate(HttpStatus httpStatus, String message, T data) {
        this.code = httpStatus.value();
        this.message = message;
        this.data = data;
        log.info("Response generated: code={}, message={}", code, message);
    }

    // 실패하였을 경우
    public RestTemplate(HttpStatus httpStatus, String message) {
        this.code = httpStatus.value();
        this.message = message;
        log.info("Response generated: code={}, message={}", code, message);
    }
}
