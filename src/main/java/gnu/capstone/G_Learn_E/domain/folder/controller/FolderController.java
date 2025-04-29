package gnu.capstone.G_Learn_E.domain.folder.controller;

import gnu.capstone.G_Learn_E.domain.folder.dto.request.CreateFolderRequest;
import gnu.capstone.G_Learn_E.domain.folder.dto.request.MoveFolderRequest;
import gnu.capstone.G_Learn_E.domain.folder.dto.request.RenameFolderRequest;
import gnu.capstone.G_Learn_E.domain.folder.dto.response.FolderResponse;
import gnu.capstone.G_Learn_E.domain.folder.dto.response.FolderTreeResponse;
import gnu.capstone.G_Learn_E.domain.folder.dto.response.FolderWorkbookRemoveResponse;
import gnu.capstone.G_Learn_E.domain.folder.dto.response.SimpleFolderResponse;
import gnu.capstone.G_Learn_E.domain.folder.entity.Folder;
import gnu.capstone.G_Learn_E.domain.folder.service.FolderService;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.service.WorkbookService;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/folder/private")
@Tag(name = "개인 폴더 API")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;
    private final WorkbookService workbookService;


    @Operation(summary = "폴더 조회", description = "폴더 + 하위폴더 + 문제집을 조회합니다.")
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

    @Operation(summary = "루트 폴더 조회", description = "루트 폴더 + 하위폴더 + 문제집을 조회합니다.")
    @GetMapping
    public ApiResponse<FolderResponse> getFolder(
            @AuthenticationPrincipal User user
    ) {
        Folder folder = folderService.getRootFolderWithChildren(user);

        FolderResponse response = createFolderResponse(folder);

        return new ApiResponse<>(HttpStatus.OK, "폴더 조회에 성공하였습니다.", response);
    }

    @Operation(summary = "폴더 트리 조회", description = "폴더 트리를 조회합니다.")
    @GetMapping("/folder-tree")
    public ApiResponse<FolderTreeResponse> getFolderTree(
            @AuthenticationPrincipal User user
    ) {
        log.info("getFolderTree request");

        List<Folder> folders = folderService.getFolderTree(user);

        Map<Long, FolderTreeResponse> responseMap = new HashMap<>();
        List<FolderTreeResponse> roots = new ArrayList<>();

        // 1. 모든 폴더를 미리 response 객체로 만들어 Map에 저장
        for (Folder folder : folders) {
            responseMap.put(folder.getId(), FolderTreeResponse.of(folder.getId(), folder.getName(), new ArrayList<>()));
        }

        // 2. 부모-자식 관계 구성
        for (Folder folder : folders) {
            FolderTreeResponse node = responseMap.get(folder.getId());
            Folder parent = folder.getParent();

            if (parent != null) {
                responseMap.get(parent.getId()).childFolders().add(node);
            } else {
                roots.add(node); // 루트 폴더
            }
        }

        FolderTreeResponse response = roots.isEmpty() ? null : roots.getFirst();

        return new ApiResponse<>(HttpStatus.OK, "폴더 트리 조회에 성공하였습니다.", response);
    }

    @Operation(summary = "폴더 생성", description = "폴더를 생성합니다.")
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

    @Operation(summary = "폴더 이동", description = "폴더를 이동합니다.")
    @PatchMapping("/{folderId}/move")
    public ApiResponse<?> moveFolder(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "folderId") Long folderId,
            @RequestBody MoveFolderRequest request
    ) {
        log.info("moveFolder request: {}", folderId);

        Folder folder = folderService.moveFolder(user, folderId, request.targetFolderId());

        SimpleFolderResponse response = SimpleFolderResponse.from(folder);

        return new ApiResponse<>(HttpStatus.OK, "폴더 이동에 성공하였습니다.", response);
    }

    @Operation(summary = "폴더 이름 변경", description = "폴더 이름을 변경합니다.")
    @PatchMapping("/{folderId}/rename")
    public ApiResponse<?> renameFolder(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "folderId") Long folderId,
            @RequestBody RenameFolderRequest request
    ) {
        log.info("renameFolder request: {}", folderId);

        Folder folder = folderService.renameFolder(user, folderId, request.newFolderName());
        SimpleFolderResponse response = SimpleFolderResponse.from(folder);
        return new ApiResponse<>(HttpStatus.OK, "폴더 이름 변경에 성공하였습니다.", response);
    }

    @Operation(summary = "폴더 삭제", description = "폴더를 삭제합니다.")
    @DeleteMapping("/{folderId}")
    public ApiResponse<?> deleteFolder(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "folderId") Long folderId
    ) {
        log.info("deleteFolder request: {}", folderId);

        folderService.deleteFolder(user, folderId);

        return new ApiResponse<>(HttpStatus.OK, "폴더 삭제에 성공하였습니다.", null);
    }

    @Operation(summary = "문제집 삭제", description = "폴더에서 문제집을 삭제합니다.")
    @DeleteMapping("/{folderId}/workbook/{workbookId}")
    public ApiResponse<?> deleteWorkbookFromFolder(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "folderId") Long folderId,
            @PathVariable(name = "workbookId") Long workbookId
    ) {
        log.info("deleteWorkbookFromFolder request: {}", folderId);

        boolean isUploadedWorkbook = folderService.deleteWorkbookFromFolder(user, folderId, workbookId);

        FolderWorkbookRemoveResponse response = FolderWorkbookRemoveResponse.of(isUploadedWorkbook);

        return new ApiResponse<>(HttpStatus.OK, "폴더에서 문제집 삭제에 성공하였습니다.", response);
    }


    private FolderResponse createFolderResponse(Folder folder) {
        if(!Hibernate.isInitialized(folder.getChildren())){
            Hibernate.initialize(folder.getChildren());
        }

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
