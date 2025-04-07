package gnu.capstone.G_Learn_E.domain.notification.service;

import gnu.capstone.G_Learn_E.domain.notification.dto.response.NotificationResponse;
import gnu.capstone.G_Learn_E.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
}
