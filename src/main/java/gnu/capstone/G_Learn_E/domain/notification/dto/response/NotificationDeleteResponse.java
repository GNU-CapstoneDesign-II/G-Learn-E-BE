package gnu.capstone.G_Learn_E.domain.notification.dto.response;

public record NotificationDeleteResponse(
        int code,
        String message
) {
    public static NotificationDeleteResponse success() {
        return new NotificationDeleteResponse(200, "알림을 삭제했습니다.");
    }
}
