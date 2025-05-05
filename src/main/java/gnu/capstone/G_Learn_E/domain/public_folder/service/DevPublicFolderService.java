package gnu.capstone.G_Learn_E.domain.public_folder.service;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.CollegeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DevPublicFolderService {

    private final CollegeRepository collegeRepository;

    @Transactional
    public College updateIsCollege(Long collegeId, boolean isCollege) {
        College college = collegeRepository.findById(collegeId).orElseThrow(() -> new RuntimeException("College not found"));
        college.setCollege(isCollege);
        return collegeRepository.save(college);
    }
}
