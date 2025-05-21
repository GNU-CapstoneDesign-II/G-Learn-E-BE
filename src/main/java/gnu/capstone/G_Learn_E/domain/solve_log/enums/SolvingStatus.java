package gnu.capstone.G_Learn_E.domain.solve_log.enums;

import lombok.Getter;

@Getter
public enum SolvingStatus {

    NOT_STARTED("기록 없음"),
    IN_PROGRESS("풀이중"),
    COMPLETED("채점 완료");

    private final String status;

    SolvingStatus(String status) {
        this.status = status;
    }
}
