package gnu.capstone.G_Learn_E.domain.folder.controller;

import gnu.capstone.G_Learn_E.domain.folder.dto.request.CreateFolderRequest;
import gnu.capstone.G_Learn_E.domain.folder.dto.request.MoveFolderRequest;
import gnu.capstone.G_Learn_E.domain.folder.dto.response.FolderResponse;
import gnu.capstone.G_Learn_E.domain.folder.entity.Folder;
import gnu.capstone.G_Learn_E.domain.folder.service.FolderService;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.service.WorkbookService;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/folder/private")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;
    private final WorkbookService workbookService;


    @PostMapping
    public ApiResponse<FolderResponse> createFolder(
            @AuthenticationPrincipal User user,
            @RequestBody CreateFolderRequest request
    ) {
        log.info("createFolder request: {}", request);

        Folder folder = folderService.createFolder(user, request.name(), request.parentId());

        FolderResponse response = createFolderResponse(folder);

        return new ApiResponse<>(HttpStatus.OK, "폴더 생성에 성공하였습니다.", response);
    }

    @GetMapping("/{folderId}")
    public ApiResponse<FolderResponse> getFolder(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "folderId") Long folderId
            ) {
        log.info("getFolder request: {}", folderId);

        Folder folder = (folderId != null) ?
                folderService.getFolderWithChildren(user, folderId)
                : folderService.getRootFolderWithChildren(user);

        FolderResponse response = createFolderResponse(folder);

        return new ApiResponse<>(HttpStatus.OK, "폴더 조회에 성공하였습니다.", response);
    }

    @GetMapping
    public ApiResponse<FolderResponse> getFolder(
            @AuthenticationPrincipal User user
    ) {
        Folder folder = folderService.getRootFolderWithChildren(user);

        FolderResponse response = createFolderResponse(folder);

        return new ApiResponse<>(HttpStatus.OK, "폴더 조회에 성공하였습니다.", response);
    }


    @PatchMapping("/{folderId}/move")
    public ApiResponse<?> moveFolder(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "folderId") Long folderId,
            @RequestBody MoveFolderRequest request
    ) {
        log.info("moveFolder request: {}", folderId);

        folderService.moveFolder(user, folderId, request.targetFolderId());

        return new ApiResponse<>(HttpStatus.OK, "폴더 이동에 성공하였습니다.", null);
    }



    private FolderResponse createFolderResponse(Folder folder) {
        List<Folder> childrenFolder = folder.getChildren();
        List<Workbook> childrenWorkbook = workbookService.getChildrenWorkbooks(folder);

        FolderResponse response = FolderResponse.of(
                folder.getId(),
                folder.getName(),
                folder.getParent() != null ? folder.getParent().getId() : null,
                folder.getCreatedAt().toString(),
                childrenFolder,
                childrenWorkbook
        );
        log.info("response: {}", response);
        return response;
    }
}
