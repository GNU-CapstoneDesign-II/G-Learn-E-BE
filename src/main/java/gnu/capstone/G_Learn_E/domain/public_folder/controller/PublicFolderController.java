package gnu.capstone.G_Learn_E.domain.public_folder.controller;

import gnu.capstone.G_Learn_E.domain.public_folder.dto.response.*;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Department;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Subject;
import gnu.capstone.G_Learn_E.domain.public_folder.service.PublicFolderService;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.service.DownloadedWorkbookService;
import gnu.capstone.G_Learn_E.global.common.dto.response.PageInfo;
import gnu.capstone.G_Learn_E.global.common.dto.response.PublicWorkbook;
import gnu.capstone.G_Learn_E.global.common.dto.serviceToController.WorkbookPaginationResult;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/folder/public")
@Tag(name = "Public 폴더 API")
@RequiredArgsConstructor
public class PublicFolderController {

    private final PublicFolderService publicFolderService;
    private final DownloadedWorkbookService downloadedWorkbookService;


    @Operation(summary = "단과대 목록 조회", description = "단과대 목록을 조회합니다.")
    @GetMapping("/colleges")
    public ApiResponse<List<CollegeResponse>> getColleges(
            @RequestParam(value = "isCollege", defaultValue = "true") boolean isCollege
    ) {
        List<College> colleges = publicFolderService.getColleges(isCollege);
        List<CollegeResponse> response = colleges.stream().map(
                CollegeResponse::from
        ).toList();
        return new ApiResponse<>(HttpStatus.OK, "단과대 목록 조회 성공", response);
    }

    @Operation(summary = "학과 목록 조회", description = "학과 목록을 조회합니다.")
    @GetMapping("/departments/{college_id}")
    public ApiResponse<List<DepartmentResponse>> getDepartments(@PathVariable("college_id") Long collegeId) {
        List<Department> departments = publicFolderService.getDepartmentsByCollegeId(collegeId);
        List<DepartmentResponse> response = departments.stream()
                .map(DepartmentResponse::from)
                .toList();
        return new ApiResponse<>(HttpStatus.OK, "학과 목록 조회 성공", response);
    }

    @Operation(summary = "과목 목록 조회", description = "과목 목록을 조회합니다.")
    @GetMapping("/subjects/{department_id}")
    public ApiResponse<List<SubjectResponse>> getSubjects(@PathVariable("department_id") Long departmentId) {
        List<Subject> subjects = publicFolderService.getSubjectsByDepartmentId(departmentId);
        List<SubjectResponse> response = subjects.stream()
                .map(SubjectResponse::from)
                .toList();

        return new ApiResponse<>(HttpStatus.OK, "과목 목록 조회 성공", response);
    }

    @Operation(summary = "문제집 목록 조회", description = "과목 폴더에서 문제집 목록을 조회합니다.")
    @GetMapping("/workbooks/{subject_id}")
    public ApiResponse<List<PublicWorkbook>> getWorkbooks(@PathVariable("subject_id") Long subjectId) {
        List<Workbook> workbooks = publicFolderService.getWorkbooksBySubjectIdWithAuthor(subjectId);
        Set<Long> usersDownloaded = downloadedWorkbookService.getUsersDownloadedWorkbookIds(subjectId);
        List<PublicWorkbook> response = workbooks.stream()
                .map(workbook -> PublicWorkbook.from(
                        workbook,
                        usersDownloaded.contains(workbook.getId())
                ))
                .toList();
        return new ApiResponse<>(HttpStatus.OK, "문제집 목록 조회 성공", response);
    }

    @GetMapping("/workbooks")
    public ApiResponse<PublicWorkbookSearchResponse> getAllWorkbooks(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order
    ) {
        WorkbookPaginationResult results = publicFolderService.getAllWorkbooks(page, size, sort, order, user);
        List<Workbook> workbooks = results.workbookList();

        Set<Long> usersDownloaded = downloadedWorkbookService.getUsersDownloadedWorkbookIds(user.getId());
        List<PublicWorkbook> publicWorkbooks = workbooks.stream()
                .map(workbook -> PublicWorkbook.from(
                        workbook,
                        usersDownloaded.contains(workbook.getId())
                ))
                .toList();
        PublicWorkbookSearchResponse response = PublicWorkbookSearchResponse.from(
                results.pageInfo(),
                publicWorkbooks
        );
        return new ApiResponse<>(HttpStatus.OK, "문제집 목록 조회 성공", response);
    }

    @GetMapping("/workbooks/college/{college_id}")
    public ApiResponse<PublicWorkbookSearchResponse> getWorkbooksByCollegeId(
            @AuthenticationPrincipal User user,
            @PathVariable("college_id") Long collegeId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order
    ) {
        WorkbookPaginationResult results = publicFolderService.getAllWorkbooksByCollegeId(collegeId, page, size, sort, order, user);
        List<Workbook> workbooks = results.workbookList();
        Set<Long> usersDownloaded = downloadedWorkbookService.getUsersDownloadedWorkbookIds(user.getId());
        List<PublicWorkbook> publicWorkbooks = workbooks.stream()
                .map(workbook -> PublicWorkbook.from(
                        workbook,
                        usersDownloaded.contains(workbook.getId())
                ))
                .toList();
        PublicWorkbookSearchResponse response = PublicWorkbookSearchResponse.from(
                results.pageInfo(),
                publicWorkbooks
        );
        return new ApiResponse<>(HttpStatus.OK, "문제집 목록 조회 성공", response);
    }

    @GetMapping("/workbooks/department/{department_id}")
    public ApiResponse<PublicWorkbookSearchResponse> getWorkbooksByDepartmentId(
            @AuthenticationPrincipal User user,
            @PathVariable("department_id") Long departmentId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order
    ) {
        WorkbookPaginationResult results = publicFolderService.getAllWorkbooksByDepartmentId(departmentId, page, size, sort, order, user);
        List<Workbook> workbooks = results.workbookList();
        Set<Long> usersDownloaded = downloadedWorkbookService.getUsersDownloadedWorkbookIds(user.getId());
        List<PublicWorkbook> publicWorkbooks = workbooks.stream()
                .map(workbook -> PublicWorkbook.from(
                        workbook,
                        usersDownloaded.contains(workbook.getId())
                ))
                .toList();
        PublicWorkbookSearchResponse response = PublicWorkbookSearchResponse.from(
                results.pageInfo(),
                publicWorkbooks
        );
        return new ApiResponse<>(HttpStatus.OK, "문제집 목록 조회 성공", response);
    }

    @GetMapping("/workbooks/subject/{subject_id}")
    public ApiResponse<PublicWorkbookSearchResponse> getWorkbooksBySubjectId(
            @AuthenticationPrincipal User user,
            @PathVariable("subject_id") Long subjectId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order
    ) {
        WorkbookPaginationResult results = publicFolderService.getAllWorkbooksBySubjectId(subjectId, page, size, sort, order, user);
        List<Workbook> workbooks = results.workbookList();

        Set<Long> usersDownloaded = downloadedWorkbookService.getUsersDownloadedWorkbookIds(user.getId());
        List<PublicWorkbook> publicWorkbooks = workbooks.stream()
                .map(workbook -> PublicWorkbook.from(
                        workbook,
                        usersDownloaded.contains(workbook.getId())
                ))
                .toList();

        PublicWorkbookSearchResponse response = PublicWorkbookSearchResponse.from(
                results.pageInfo(),
                publicWorkbooks
        );

        return new ApiResponse<>(HttpStatus.OK, "문제집 목록 조회 성공", response);
    }
}
