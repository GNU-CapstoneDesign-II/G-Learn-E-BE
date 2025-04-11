package gnu.capstone.G_Learn_E.domain.notification.controller;

import gnu.capstone.G_Learn_E.domain.notification.dto.response.NotificationResponse;
import gnu.capstone.G_Learn_E.domain.notification.service.NotificationService;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getNotificationsByUserId(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByUser(user, offset, limit);
        return new ApiResponse<>(HttpStatus.OK, "알림 조회에 성공했습니다.", notifications);
    }


    @DeleteMapping("/{notificationId}")
    public ApiResponse<?> deleteNotification(
            @AuthenticationPrincipal User user,
            @PathVariable Long notificationId
    ) {
        notificationService.deleteNotification(user.getId(), notificationId);
        return new ApiResponse<>(HttpStatus.OK, "알림 삭제에 성공했습니다.");
    }
}
