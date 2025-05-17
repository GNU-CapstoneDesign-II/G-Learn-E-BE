package gnu.capstone.G_Learn_E.domain.user.dto.response;

import gnu.capstone.G_Learn_E.domain.public_folder.dto.response.CollegeResponse;
import gnu.capstone.G_Learn_E.domain.public_folder.dto.response.DepartmentResponse;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.global.common.dto.response.PageInfo;

import java.util.ArrayList;
import java.util.List;

public record UserRankingPageResponse(
        PageInfo pageInfo,
        List<UserRankingResponse> rankings
) {
    public static UserRankingPageResponse from(
            PageInfo pageInfo,
            List<User> rankings,
            long startRank
    ) {
        List<UserRankingResponse> list = new ArrayList<>();
        if (rankings.isEmpty()) {
            return new UserRankingPageResponse(pageInfo, list);
        }

        // 첫 유저
        User first = rankings.get(0);
        long prevRank  = startRank;
        short prevLevel = first.getLevel();
        int prevExp     = first.getExp();
        int sameCount   = 1;

        list.add(UserRankingResponse.from(first, prevRank));

        // 나머지 유저들: stop-counting 방식
        for (int i = 1; i < rankings.size(); i++) {
            User u = rankings.get(i);
            long rank;
            if (u.getLevel() == prevLevel && u.getExp() == prevExp) {
                // 동점: 같은 순위 유지
                rank = prevRank;
                sameCount++;
            } else {
                // 점수 ↓ : 이전 순위 + 동점자 수
                rank = prevRank + sameCount;
                prevLevel = u.getLevel();
                prevExp   = u.getExp();
                prevRank  = rank;
                sameCount = 1;
            }
            list.add(UserRankingResponse.from(u, rank));
        }

        return new UserRankingPageResponse(pageInfo, list);
    }

    record UserRankingResponse(
            Long id,
            String nickname,
            Integer profileImage,
            Short level,
            Long ranking,
            long createdWorkbooks,
            long solvedWorkbooks,
            CollegeResponse college,
            DepartmentResponse department
    ) {
        public static UserRankingResponse from(
                User user,
                long ranking
        ) {
            return new UserRankingResponse(
                    user.getId(),
                    user.getNickname(),
                    user.getProfileImage(),
                    user.getLevel(),
                    ranking,
                    user.getCreateWorkbookCount(),
                    user.getSolvedWorkbookCount(),
                    CollegeResponse.from(user.getCollege()),
                    DepartmentResponse.from(user.getDepartment())
            );
        }
    }
}
