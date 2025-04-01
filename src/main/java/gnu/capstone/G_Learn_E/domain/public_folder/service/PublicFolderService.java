package gnu.capstone.G_Learn_E.domain.public_folder.service;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Department;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Subject;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.SubjectWorkbookMap;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.CollegeRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.DepartmentRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.SubjectRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.SubjectWorkbookMapRepository;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicFolderService {

    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectWorkbookMapRepository subjectWorkbookMapRepository;

    // TODO : 커리큘럼 폴더 서비스 구현
    public List<College> getColleges() {
        return collegeRepository.findAll();
    }

    public List<Department> getDepartmentsByCollegeId(Long collegeId) {
        return departmentRepository.findByCollegeId(collegeId);
    }
    public List<Subject> getSubjectsByDepartmentId(Long departmentId) {
        return subjectRepository.findByDepartmentId(departmentId);
    }
    @Transactional
    public List<Workbook> getWorkbooksBySubjectId(Long subjectId) {
        List<SubjectWorkbookMap> maps = subjectWorkbookMapRepository.findAllBySubject_Id(subjectId);

        return maps.stream()
                .map(SubjectWorkbookMap::getWorkbook)
                .collect(Collectors.toList());
    }

}
