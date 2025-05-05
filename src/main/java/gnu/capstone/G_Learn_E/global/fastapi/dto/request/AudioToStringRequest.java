package gnu.capstone.G_Learn_E.global.fastapi.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record AudioToStringRequest(
        MultipartFile audioFile
) {
    public static AudioToStringRequest of(MultipartFile audioFile) {
        return new AudioToStringRequest(audioFile);
    }
}
