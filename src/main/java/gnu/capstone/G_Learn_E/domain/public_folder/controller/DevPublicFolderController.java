package gnu.capstone.G_Learn_E.domain.public_folder.controller;

import gnu.capstone.G_Learn_E.domain.public_folder.dto.request.UpdateCollege;
import gnu.capstone.G_Learn_E.domain.public_folder.service.DevPublicFolderService;
import gnu.capstone.G_Learn_E.global.template.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/dev/api/folder/public")
@Tag(name = "개발자용 Public 폴더 API")
@RequiredArgsConstructor
public class DevPublicFolderController {

    private final DevPublicFolderService devPublicFolderService;


    @Operation(summary = "단과대 isCollege 업데이트", description = "단과대 isCollege를 업데이트합니다.")
    @PatchMapping("/college/{collegeId}/isCollege")
    public ApiResponse<?> updateIsCollege(@PathVariable String collegeId, @RequestBody UpdateCollege request) {
        devPublicFolderService.updateIsCollege(Long.parseLong(collegeId), request.isCollege());
        return new ApiResponse<>(HttpStatus.OK, "단과대 isCollege 업데이트 성공", null);
    }
}
