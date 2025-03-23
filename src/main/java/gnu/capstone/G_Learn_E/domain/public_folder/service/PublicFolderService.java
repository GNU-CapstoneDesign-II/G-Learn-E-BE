package gnu.capstone.G_Learn_E.domain.public_folder.service;

import gnu.capstone.G_Learn_E.domain.public_folder.repository.CollegeRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.DepartmentRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.SubjectRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.SubjectWorkbookMapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicFolderService {

    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectWorkbookMapRepository subjectWorkbookMapRepository;

    // TODO : 커리큘럼 폴더 서비스 구현
}
