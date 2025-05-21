package gnu.capstone.G_Learn_E.domain.user.repository;

import gnu.capstone.G_Learn_E.domain.user.entity.ActivityLog;
import gnu.capstone.G_Learn_E.domain.user.enums.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    @Query("""
        SELECT FUNCTION('DATE', a.createdAt)      AS day,
               COUNT(a)                           AS cnt
          FROM ActivityLog a
         WHERE a.user.id        = :userId
           AND a.activityType   IN :types
           AND a.createdAt      >= :startDate
         GROUP BY FUNCTION('DATE', a.createdAt)
         ORDER BY FUNCTION('DATE', a.createdAt)
    """)
    List<DailyCount> countDailyByUserAndTypesSince(
            @Param("userId")    Long userId,
            @Param("types")     List<ActivityType> types,
            @Param("startDate") LocalDateTime startDate
    );

    interface DailyCount {
        LocalDate getDay();
        Long      getCnt();
    }
}
