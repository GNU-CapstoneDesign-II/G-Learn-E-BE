package gnu.capstone.G_Learn_E.domain.folder.controller;

import gnu.capstone.G_Learn_E.domain.folder.service.FolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/folder/private")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    // TODO : 폴더 컨트롤러 구현
}
