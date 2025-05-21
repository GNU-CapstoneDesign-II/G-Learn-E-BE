package gnu.capstone.G_Learn_E.domain.user.service;

import gnu.capstone.G_Learn_E.domain.user.dto.response.DailyActivityCountResponse;
import gnu.capstone.G_Learn_E.domain.user.entity.ActivityLog;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.user.enums.ActivityType;
import gnu.capstone.G_Learn_E.domain.user.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    /**
     * @param userId 조회할 유저 ID
     * @param types  포함할 ActivityType 목록
     * @param days   조회 기간(오늘 포함, 일수)
     */
    public List<DailyActivityCountResponse> getDailyCounts(
            Long userId,
            List<ActivityType> types,
            int days
    ) {
        LocalDate today     = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1);
        LocalDateTime start = startDate.atStartOfDay();

        // DB에서 집계
        List<ActivityLogRepository.DailyCount> raw =
                activityLogRepository.countDailyByUserAndTypesSince(userId, types, start);

        // Map<날짜, 개수>
        Map<LocalDate, Long> countMap = raw.stream()
                .collect(Collectors.toMap(ActivityLogRepository.DailyCount::getDay, ActivityLogRepository.DailyCount::getCnt));

        // 기간 내 모든 날짜 채우기 (존재하지 않으면 0)
        return IntStream.range(0, days + 1)
                .mapToObj(i -> {
                    LocalDate d = startDate.plusDays(i);
                    return DailyActivityCountResponse.of(d, countMap.getOrDefault(d, 0L));
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ActivityLog saveActivityLog(
            ActivityType activityType,
            User user
    ) {
        ActivityLog activityLog = ActivityLog.builder()
                .user(user)
                .activityType(activityType)
                .build();
        return activityLogRepository.save(activityLog);
    }
}
