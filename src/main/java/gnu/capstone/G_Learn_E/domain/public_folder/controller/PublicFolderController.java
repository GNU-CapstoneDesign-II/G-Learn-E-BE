package gnu.capstone.G_Learn_E.domain.public_folder.controller;

import gnu.capstone.G_Learn_E.domain.public_folder.dto.response.*;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Department;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Subject;
import gnu.capstone.G_Learn_E.domain.public_folder.service.PublicFolderService;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/folder/public")
@RequiredArgsConstructor
public class PublicFolderController {

    private final PublicFolderService publicFolderService;

    // TODO : 공용 폴더 컨트롤러 구현
    @GetMapping("/colleges")
    public ApiResponse<List<CollegeResponse>> getColleges() {
        List<College> colleges = publicFolderService.getColleges();
        List<CollegeResponse> response = colleges.stream().map(
                CollegeResponse::from
        ).toList();
        return new ApiResponse<>(HttpStatus.OK, "단과대 목록 조회 성공", response);
    }
    @GetMapping("/departments/{college_id}")
    public ApiResponse<List<DepartmentResponse>> getDepartments(@PathVariable("college_id") Long collegeId) {
        List<Department> departments = publicFolderService.getDepartmentsByCollegeId(collegeId);
        List<DepartmentResponse> response = departments.stream()
                .map(DepartmentResponse::from)
                .toList();
        return new ApiResponse<>(HttpStatus.OK, "학과 목록 조회 성공", response);
    }
    @GetMapping("/subjects/{department_id}")
    public ApiResponse<List<SubjectResponse>> getSubjects(@PathVariable("department_id") Long departmentId) {
        List<Subject> subjects = publicFolderService.getSubjectsByDepartmentId(departmentId);
        List<SubjectResponse> response = subjects.stream()
                .map(SubjectResponse::from)
                .toList();

        return new ApiResponse<>(HttpStatus.OK, "과목 목록 조회 성공", response);
    }
    @GetMapping("/workbooks/{subject_id}")
    public ApiResponse<List<WorkbookResponse>> getWorkbooks(@PathVariable("subject_id") Long subjectId) {

        List<Workbook> workbooks = publicFolderService.getWorkbooksBySubjectIdWithAuthor(subjectId);
        List<WorkbookResponse> response = workbooks.stream()
                .map(WorkbookResponse::from)
                .toList();
        return new ApiResponse<>(HttpStatus.OK, "문제집 목록 조회 성공", response);
    }
}
