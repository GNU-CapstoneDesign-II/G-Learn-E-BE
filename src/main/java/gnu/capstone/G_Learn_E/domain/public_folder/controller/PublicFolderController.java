package gnu.capstone.G_Learn_E.domain.public_folder.controller;

import gnu.capstone.G_Learn_E.domain.public_folder.dto.response.*;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Department;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Subject;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.SubjectRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.service.PublicFolderService;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.global.template.RestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/folder/public")
@RequiredArgsConstructor
public class PublicFolderController {

    private final PublicFolderService publicFolderService;

    // TODO : 공용 폴더 컨트롤러 구현
    @GetMapping("/colleges")
    public RestTemplate<List<CollegeResponse>> getColleges() {
        List<College> colleges = publicFolderService.getColleges();
        List<CollegeResponse> response = colleges.stream().map(
                CollegeResponse::from
        ).toList();
        return new RestTemplate<>(HttpStatus.OK, "단과대 목록 조회 성공", response);
    }
    @GetMapping("/departments/{college_id}")
    public RestTemplate<List<DepartmentResponse>> getDepartments(@PathVariable("college_id") Long collegeId) {
        List<Department> departments = publicFolderService.getDepartmentsByCollegeId(collegeId);
        List<DepartmentResponse> response = departments.stream()
                .map(DepartmentResponse::from)
                .toList();
        return new RestTemplate<>(HttpStatus.OK, "학과 목록 조회 성공", response);
    }
    @GetMapping("/subjects/{department_id}")
    public RestTemplate<List<SubjectResponse>> getSubjects(@PathVariable("department_id") Long departmentId) {
        List<Subject> subjects = publicFolderService.getSubjectsByDepartmentId(departmentId);
        List<SubjectResponse> response = subjects.stream()
                .map(SubjectResponse::from)
                .toList();

        return new RestTemplate<>(HttpStatus.OK, "과목 목록 조회 성공", response);
    }
    @GetMapping("/workbooks/{subject_id}")
    public RestTemplate<List<WorkbookResponse>> getWorkbooks(@PathVariable("subject_id") Long subjectId) {

        List<Workbook> workbooks = publicFolderService.getWorkbooksBySubjectId(subjectId);
        List<WorkbookResponse> response = workbooks.stream()
                .map(WorkbookResponse::from)
                .toList();
        return new RestTemplate<>(HttpStatus.OK, "문제집 목록 조회 성공", response);
    }
}
