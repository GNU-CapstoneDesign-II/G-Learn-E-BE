package gnu.capstone.G_Learn_E.domain.notification.controller;

import gnu.capstone.G_Learn_E.domain.notification.dto.response.NotificationDeleteResponse;
import gnu.capstone.G_Learn_E.domain.notification.dto.response.NotificationResponse;
import gnu.capstone.G_Learn_E.domain.notification.service.NotificationService;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getNotificationsByUserId(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByUser(user.getId(), offset, limit);
        return new ApiResponse<>(HttpStatus.OK, "알림 조회에 성공했습니다.", notifications);
    }


    @DeleteMapping("/{notificationId}")
    public ResponseEntity<NotificationDeleteResponse> deleteNotification(
            @AuthenticationPrincipal User user,
            @PathVariable Long notificationId
    ) {
        notificationService.deleteNotification(user.getId(), notificationId);
        return ResponseEntity.ok(NotificationDeleteResponse.success());
    }



}
