package gnu.capstone.G_Learn_E.domain.public_folder.service;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Department;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Subject;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.CollegeRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.DepartmentRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.SubjectRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.SubjectWorkbookMapRepository;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.repository.WorkbookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public College getCollege(Long collegeId) {
        return collegeRepository.findById(collegeId)
                .orElseThrow(() -> new RuntimeException("College not found"));
    }

    public List<College> getColleges() {
        return collegeRepository.findAll();
    }
    public List<College> getColleges(boolean isCollege) {
        if (isCollege) {
            return collegeRepository.findAllByCollegeTrue();
        } else {
            return collegeRepository.findAllByCollegeFalse();
        }
    }

    public Department getDepartmentByCollegeId(Long collegeId, Long departmentId) {
        return departmentRepository.findByIdAndCollegeId(departmentId, collegeId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
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

    public long uploadedWorkbookCountByUser(User user) {
        return subjectWorkbookMapRepository.countByWorkbook_Author_Id(user.getId());
    }


    public List<Workbook> getAllWorkbooks(int page, int size, String sort, String order) {
        return workbookRepository.findAllWorkbooks(getPageable(page, size, sort, order)).getContent();
    }

    public List<Workbook> getAllWorkbooksByCollegeId(Long collegeId, int page, int size, String sort, String order) {
        return workbookRepository.findAllByCollegeId(collegeId, getPageable(page, size, sort, order)).getContent();
    }

    public List<Workbook> getAllWorkbooksByDepartmentId(Long departmentId, int page, int size, String sort, String order) {
        return workbookRepository.findAllByDepartmentId(departmentId, getPageable(page, size, sort, order)).getContent();
    }

    public List<Workbook> getAllWorkbooksBySubjectId(Long subjectId, int page, int size, String sort, String order) {
        return workbookRepository.findAllBySubjectId(subjectId, getPageable(page, size, sort, order)).getContent();
    }

    private Pageable getPageable(int page, int size, String sort, String order) {
        if(!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException("Invalid order: " + order);
        }
        if (page < 0 || size <= 0 || size > 100) {
            throw new IllegalArgumentException("Invalid page or size: " + page + ", " + size);
        }
        if(!sort.equals("createdAt") && !sort.equals("name") && !sort.equals("author")) {
            throw new IllegalArgumentException("Invalid sort: " + sort);
        }
        if(sort.equals("author")) {
            sort = "author.name";
        }
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, sort));
    }
}
