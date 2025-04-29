package gnu.capstone.G_Learn_E.domain.public_folder.service;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Department;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Subject;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.CollegeRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.DepartmentRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.SubjectRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.SubjectWorkbookMapRepository;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.repository.WorkbookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicFolderService {

    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectWorkbookMapRepository subjectWorkbookMapRepository;
    private final WorkbookRepository workbookRepository;

    // TODO : 커리큘럼 폴더 서비스 구현
    public List<College> getColleges() {
        return collegeRepository.findAll();
    }

    public List<Department> getDepartmentsByCollegeId(Long collegeId) {
        return departmentRepository.findByCollegeId(collegeId);
    }
    public List<Subject> getSubjectsByDepartmentId(Long departmentId) {
        List<Subject> subjects = subjectRepository.findByDepartmentId(departmentId);

        // 학년 -> 교과목명 순으로 정렬
        subjects.sort(Comparator
                .comparingInt((Subject s) -> s.getGrade().getOrder())
                .thenComparing(Subject::getName)
        );
        return subjects;
    }

    public List<Workbook> getWorkbooksBySubjectIdWithAuthor(Long subjectId) {
        return workbookRepository.findAllWithAuthorBySubjectId(subjectId);
    }

    public Subject findSubjectInTree(Long collegeId, Long departmentId, Long subjectId) {
        // collegeId, departmentId, subjectId로 Subject 조회
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new RuntimeException("College not found"));
        Department department = departmentRepository.findByIdAndCollegeId(departmentId, collegeId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        return subjectRepository.findByIdAndDepartmentId(subjectId, departmentId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
    }

    public boolean isPublicWorkbook(Long workbookId) {
        return subjectWorkbookMapRepository.existsByWorkbook_Id(workbookId);
    }

}
