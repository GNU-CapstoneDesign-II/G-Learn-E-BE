package gnu.capstone.G_Learn_E.domain.notification.dto.response;

import gnu.capstone.G_Learn_E.domain.notification.entity.Notification;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String content,
        boolean isRead,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getContent(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
