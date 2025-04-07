package gnu.capstone.G_Learn_E.domain.notification.controller;

import gnu.capstone.G_Learn_E.domain.notification.dto.response.NotificationResponse;
import gnu.capstone.G_Learn_E.domain.notification.service.NotificationService;
import gnu.capstone.G_Learn_E.global.template.RestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    public RestTemplate<Map<String, Object>> getNotificationsByUserId(
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByUser(userId, offset, limit);

        Map<String, Object> data = new HashMap<>();
        data.put("notifications", notifications);

        return new RestTemplate<>(HttpStatus.OK, "알림 조회에 성공했습니다.", data);
    }
}
