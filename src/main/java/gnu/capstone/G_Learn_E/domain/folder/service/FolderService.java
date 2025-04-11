package gnu.capstone.G_Learn_E.domain.folder.service;

import gnu.capstone.G_Learn_E.domain.folder.entity.Folder;
import gnu.capstone.G_Learn_E.domain.folder.repository.FolderRepository;
import gnu.capstone.G_Learn_E.domain.folder.repository.FolderWorkbookMapRepository;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
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


    @Transactional
    public void moveFolder(User user, Long folderId, Long targetParentId) {
        log.info("moveFolder request: {}", folderId);
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        if(!folder.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You do not have permission to move this folder");
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
    }


    private boolean isCircularMove(Folder folder, Folder newParent) {
        Folder current = newParent;
        while (current != null) {
            if (current.getId().equals(folder.getId())) return true;
            current = current.getParent();
        }
        return false;
    }


}
