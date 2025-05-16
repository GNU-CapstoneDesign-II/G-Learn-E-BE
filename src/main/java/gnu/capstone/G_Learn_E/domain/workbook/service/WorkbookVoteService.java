package gnu.capstone.G_Learn_E.domain.workbook.service;

import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.entity.WorkbookVote;
import gnu.capstone.G_Learn_E.domain.workbook.enums.WorkbookVoteType;
import gnu.capstone.G_Learn_E.domain.workbook.repository.WorkbookVoteRepository;
import gnu.capstone.G_Learn_E.domain.workbook.repository.WorkbookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkbookVoteService {

    private final WorkbookRepository workbookRepository;
    private final WorkbookVoteRepository workbookVoteRepository;

    @Transactional
    public void toggleVote(Workbook workbook, User user, String voteTypeStr) {
        WorkbookVoteType workbookVoteType = (voteTypeStr.equalsIgnoreCase("like")) ? WorkbookVoteType.LIKE : WorkbookVoteType.DISLIKE;

        Optional<WorkbookVote> voteOpt = workbookVoteRepository.findByUserIdAndWorkbookId(user.getId(), workbook.getId());
        if(voteOpt.isEmpty()){
            // 한글로 주석
            // 사용자가 아직 투표하지 않은 경우, 새로운 투표를 생성합니다.
            WorkbookVote workbookVote = WorkbookVote.builder()
                    .user(user)
                    .workbook(workbook)
                    .voteType(workbookVoteType)
                    .build();
            workbookVoteRepository.save(workbookVote);
        } else {
            // 사용자가 이미 투표한 경우, 투표를 토글합니다.
            WorkbookVote existingVote = voteOpt.get();
            if (existingVote.getVoteType() == workbookVoteType) {
                // 같은 투표를 다시 클릭하면 투표를 제거합니다.
                workbookVoteRepository.delete(existingVote);
            } else {
                // 다른 투표를 클릭하면 기존 투표를 업데이트합니다.
                existingVote.setVoteType(workbookVoteType);
                workbookVoteRepository.save(existingVote);
            }
        }
    }

    @Transactional
    public Workbook updateVoteCount(Workbook workbook){
        Long likeCount = workbookVoteRepository.countByWorkbookIdAndVoteType(workbook.getId(), WorkbookVoteType.LIKE);
        Long dislikeCount = workbookVoteRepository.countByWorkbookIdAndVoteType(workbook.getId(), WorkbookVoteType.DISLIKE);

        workbook.setLikeCount(likeCount);
        workbook.setDislikeCount(dislikeCount);

        return workbookRepository.save(workbook);
    }
}
