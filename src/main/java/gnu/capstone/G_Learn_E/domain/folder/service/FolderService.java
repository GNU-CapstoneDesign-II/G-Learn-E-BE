package gnu.capstone.G_Learn_E.domain.folder.service;

import gnu.capstone.G_Learn_E.domain.folder.entity.Folder;
import gnu.capstone.G_Learn_E.domain.folder.entity.FolderWorkbookId;
import gnu.capstone.G_Learn_E.domain.folder.entity.FolderWorkbookMap;
import gnu.capstone.G_Learn_E.domain.folder.repository.FolderRepository;
import gnu.capstone.G_Learn_E.domain.folder.repository.FolderWorkbookMapRepository;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.repository.WorkbookRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final WorkbookRepository workbookRepository;
    private final FolderWorkbookMapRepository folderWorkbookMapRepository;


    public Folder createFolder(User user, String folderName, Long parentId) {
        log.info("createFolder request: {}", folderName);
        if (parentId == null) {
            throw new IllegalArgumentException("Parent folder ID cannot be null");
        }

        Folder parentFolder = folderRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent folder not found"));

        if (!parentFolder.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You do not have permission to create a folder in this parent folder");
        }

        Folder folder = Folder.builder()
                .name(folderName)
                .user(user)
                .parent(parentFolder)
                .build();

        return folderRepository.save(folder);
    }

    public Folder getRootFolder(User user) {
        log.info("getRootFolder request");
        return folderRepository.findByUserAndParentIsNull(user)
                .orElseThrow(() -> new IllegalArgumentException("Root folder not found"));
    }
    public Folder getRootFolderWithChildren(User user) {
        // Children만 left join으로 가져옴
        log.info("getRootFolder request");
        return folderRepository.findByUserAndParentIsNullWithChildren(user)
                .orElseThrow(() -> new IllegalArgumentException("Root folder not found"));
    }

    public Folder getFolder(User user, Long folderId) {
        log.info("getFolder request: {}", folderId);
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        if(!folder.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You do not have permission to access this folder");
        }
        return folder;
    }
    public Folder getFolderWithChildren(User user, Long folderId) {
        // Children만 left join으로 가져옴
        log.info("getFolder request: {}", folderId);
        Folder folder = folderRepository.findByIdWithChildren(folderId)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        if(!folder.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You do not have permission to access this folder");
        }
        return folder;
    }

    @Transactional(readOnly = true)
    public List<Folder> getFolderTree(User user) {
        log.info("getFolderTree request");
        return folderRepository.findAllByUserWithParent(user);
    }

    // 유저의 개인 폴더 안에 존재하는 문제집 ID 인지 검사
    public boolean isWorkbookInUserFolder(User user, Long workbookId) {
        log.info("isWorkbookInUserFolder request: {}", workbookId);
        return folderRepository.existsByUserAndFolderWorkbookMaps_Workbook_Id(user, workbookId);
    }


    @Transactional
    public Folder moveFolder(User user, Long folderId, Long targetParentId) {
        log.info("moveFolder request: {}", folderId);
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        if(!folder.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You do not have permission to move this folder");
        }
        if(folderId == getRootFolder(user).getId()) {
            throw new IllegalArgumentException("Cannot move root folder");
        }


        Folder targetParent = folderRepository.findById(targetParentId)
                .orElseThrow(() -> new IllegalArgumentException("Target parent folder not found"));
        if(!targetParent.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You do not have permission to move this folder");
        }

        if(isCircularMove(folder, targetParent)) {
            throw new IllegalArgumentException("Cannot move folder to a child folder");
        }

        folder.setParent(targetParent);

        return folderRepository.save(folder);
    }


    @Transactional
    public Folder renameFolder(User user, Long folderId, String newName) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        if(!folder.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You do not have permission to rename this folder");
        }

        folder.setName(newName);
        return folder;
    }

    @Transactional
    public void deleteFolder(User user, Long folderId) {
        Folder folder = folderRepository.findByIdWithChildren(folderId)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        if(!folder.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You do not have permission to delete this folder");
        }

        Folder root = getRootFolder(user);

        // 루트 폴더 삭제 불가
        if (folder.getId().equals(root.getId())) {
            throw new IllegalStateException("루트 폴더는 삭제할 수 없습니다.");
        }

        // 하위 폴더 존재 검사
        if (!folder.getChildren().isEmpty()) {
            throw new IllegalStateException("하위 폴더가 존재하여 삭제할 수 없습니다.");
        }

        // 문제집 매핑 존재 검사
        if (!folder.getFolderWorkbookMaps().isEmpty()) {
            throw new IllegalStateException("폴더에 연결된 문제집이 있어 삭제할 수 없습니다.");
        }

        // 폴더 삭제
        folderRepository.delete(folder);
    }


    private boolean isCircularMove(Folder folder, Folder newParent) {
        Folder current = newParent;
        while (current != null) {
            if (current.getId().equals(folder.getId())) return true;
            current = current.getParent();
        }
        return false;
    }

    @Transactional
    public boolean deleteWorkbookFromFolder(User user, Long folderId, Long workbookId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("폴더를 찾을 수 없습니다."));

        if (!folder.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("해당 폴더에 대한 권한이 없습니다.");
        }

        FolderWorkbookId id = new FolderWorkbookId(folderId, workbookId);
        FolderWorkbookMap map = folderWorkbookMapRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 폴더에 연결된 문제집이 없습니다."));

        Workbook workbook = map.getWorkbook();

        folderWorkbookMapRepository.delete(map);

        // Workbook 엔티티 자체를 삭제하는 것이 아님
        // 어떠한 Mapping도 가지지 않는 Workbook을 주기적으로 삭제해주는 Schedular 구현 필요

        // 업로드 여부를 반환
        return workbook.isUploaded();
    }

    @Transactional
    public Folder moveWorkbook(User user, Long workbookId, Long targetFolderId) {
        // 1) 사용자의 대상 폴더 조회 및 검증
        Folder target = folderRepository.findByIdAndUser(targetFolderId, user)
                .orElseThrow(() -> new RuntimeException("대상 폴더를 찾을 수 없습니다."));

        // 2) 사용자가 작성한 워크북 조회 및 검증
        if(!isWorkbookInUserFolder(user, workbookId)) {
            throw new RuntimeException("문제집 접근 권한이 없습니다.");
        }
        Workbook wb = workbookRepository.findById(workbookId)
                .orElseThrow(() -> new RuntimeException("워크북을 찾을 수 없습니다."));

        // 3) 기존 매핑 삭제
        FolderWorkbookMap oldMap = folderWorkbookMapRepository.findByWorkbook_Id(workbookId)
                .orElseThrow(() -> new RuntimeException("이 워크북의 기존 폴더 매핑이 없습니다."));
        folderWorkbookMapRepository.delete(oldMap);

        // 4) 새 매핑 생성·저장
        FolderWorkbookMap newMap = FolderWorkbookMap.builder()
                .folder(target)
                .workbook(wb)
                .build();
        folderWorkbookMapRepository.save(newMap);

        return target;
    }

    public Folder findWorkbookFolder(User user, Long workbookId) {
        log.info("findWorkbookFolder request: {}", workbookId);
        Folder folder = folderRepository.findFolderOfUserWorkbook(user.getId(), workbookId)
                .orElseThrow(() -> new IllegalArgumentException("Folder or Workbook not found"));
        if(!folder.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You do not have permission to access this folder");
        }
        return folder;
    }
}
