package gnu.capstone.G_Learn_E.global.search.controller;

import gnu.capstone.G_Learn_E.domain.folder.entity.Folder;
import gnu.capstone.G_Learn_E.domain.folder.service.FolderService;
import gnu.capstone.G_Learn_E.domain.public_folder.service.PublicFolderService;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.service.DownloadedWorkbookService;
import gnu.capstone.G_Learn_E.global.common.dto.response.PublicPath;
import gnu.capstone.G_Learn_E.global.search.dto.response.SearchResponse;
import gnu.capstone.G_Learn_E.global.search.service.SearchService;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    // 통합 검색 = total
    // 문제집 제목 검색 = title
    // 생성자 검색 = author
    // 문제집 내부 문제 텍스트의 키워드로 검색 = content

    private final SearchService searchService;
    private final DownloadedWorkbookService downloadedWorkbookService;
    private final FolderService folderService;
    private final PublicFolderService publicFolderService;

    @GetMapping
    @Operation(
            summary = "워크북 검색",
            description = """
                    워크북 검색 API
                    - keyword : 검색어
                    - range : 공개 범위 (all | private | public)
                    - type : 검색 대상 (total | title | author | content)
                    - page : 페이지 번호 (0-base)
                    - size : 페이지 크기
                    - sort : 정렬 키 (relevance | createdAt | title | author)
                    - order : 정렬 순서 (asc | desc)
                    """
    )
    public ApiResponse<?> search(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "range", defaultValue = "all") String range,
            @RequestParam(value = "type", defaultValue = "total") String type,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "7") int size,
            @RequestParam(value = "sort", defaultValue = "relevance") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order
    ){
        List<Workbook> privateWorkbooks = null, publicWorkbooks = null;
        Set<Long> usersDownloaded = null;
        Map<Long, Folder> privatePaths = new HashMap<>();
        Map<Long, List<PublicPath>> publicPaths = new HashMap<>();

        if(range.equals("all") || range.equals("private")) {
            privateWorkbooks = searchService.searchWorkbook(user, keyword, "private", type, page, size, sort, order);
            privateWorkbooks.forEach(workbook -> {
                Folder folder = folderService.findWorkbookFolder(user, workbook.getId());
                privatePaths.put(workbook.getId(), folder);
            });
        }
        if(range.equals("all") || range.equals("public")) {
            publicWorkbooks = searchService.searchWorkbook(user, keyword, "public", type, page, size, sort, order);
            usersDownloaded = downloadedWorkbookService.getUsersDownloadedWorkbookIds(user.getId());
            publicWorkbooks.forEach(workbook -> {
                List<PublicPath> paths = publicFolderService.getPublicPath(workbook);
                publicPaths.put(workbook.getId(), paths);
            });
        }

        SearchResponse response = SearchResponse.from(
                privateWorkbooks,
                privatePaths,
                publicWorkbooks,
                usersDownloaded,
                publicPaths
        );

        return new ApiResponse<>(HttpStatus.OK, "검색 결과", response);
    }
}
