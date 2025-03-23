package gnu.capstone.G_Learn_E.domain.public_folder.controller;

import gnu.capstone.G_Learn_E.domain.public_folder.service.PublicFolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/folder/public")
@RequiredArgsConstructor
public class PublicFolderController {

    private final PublicFolderService publicFolderService;

    // TODO : 공용 폴더 컨트롤러 구현
}
