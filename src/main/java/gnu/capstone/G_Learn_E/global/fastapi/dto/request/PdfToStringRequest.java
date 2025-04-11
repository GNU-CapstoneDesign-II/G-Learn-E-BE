package gnu.capstone.G_Learn_E.global.fastapi.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record PdfToStringRequest(
        MultipartFile pdfFile
) {
    public static PdfToStringRequest of(MultipartFile pdfFile) {
        return new PdfToStringRequest(pdfFile);
    }
}
