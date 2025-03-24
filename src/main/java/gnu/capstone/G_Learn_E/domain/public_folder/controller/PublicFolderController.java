package gnu.capstone.G_Learn_E.domain.public_folder.controller;

import gnu.capstone.G_Learn_E.domain.public_folder.dto.response.CollegeResponse;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import gnu.capstone.G_Learn_E.domain.public_folder.service.PublicFolderService;
import gnu.capstone.G_Learn_E.global.template.RestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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
    public RestTemplate<List<CollegeResponse>> getColleges() {
        List<College> colleges = publicFolderService.getColleges();
        List<CollegeResponse> response = colleges.stream().map(
                CollegeResponse::from
        ).toList();
        return new RestTemplate<>(HttpStatus.OK, "단과대 목록 조회 성공", response);
    }
}
