package gnu.capstone.G_Learn_E.domain.notification.service;

import gnu.capstone.G_Learn_E.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // TODO : 알림 서비스 구현
}
