package gnu.capstone.G_Learn_E.global.file_upload.controller;

import gnu.capstone.G_Learn_E.global.file_upload.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "파일 업로드 API, 향후 admin API로 이동 예정")
@RequestMapping("/dev/api/fileupload")
public class FileUploadController {

    private final FileUploadService fileUploadService;


    @Operation(summary = "파일 업로드", description = "경상대 개설과목 json 파일을 업로드합니다.")
    @PostMapping("/upload/public_folder_metadata")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            long size = file.getSize();
            System.out.println("파일명: " + originalFilename + ", 크기: " + size);

            // 1. 임시 폴더 생성 및 파일 저장
            Path tempDir = Files.createTempDirectory("");
            Path tempFilePath = tempDir.resolve(originalFilename);
            file.transferTo(tempFilePath.toFile());

            // 2. 비동기로 파일 파싱 + DB 적재
            fileUploadService.processUploadedFile(tempFilePath);

            return ResponseEntity.ok("파일 업로드 성공, 비동기 처리 시작: " + originalFilename);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("업로드 실패: " + e.getMessage());
        }
    }
}
