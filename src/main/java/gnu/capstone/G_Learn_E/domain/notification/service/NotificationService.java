package gnu.capstone.G_Learn_E.domain.notification.service;

import gnu.capstone.G_Learn_E.domain.notification.dto.response.NotificationResponse;
import gnu.capstone.G_Learn_E.domain.notification.entity.Notification;
import gnu.capstone.G_Learn_E.domain.notification.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResponse> getNotificationsByUser(Long userId, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        return notificationRepository.findAllByUserId(userId, pageable)
                .map(NotificationResponse::from)
                .toList(); // ✅ Page<Notification>이므로 map 사용 가능
    }
    public void deleteNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 알림이 존재하지 않습니다. id=" + notificationId));

        if (!notification.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("해당 알림을 삭제할 권한이 없습니다.");
        }

        notificationRepository.delete(notification);
    }

}
