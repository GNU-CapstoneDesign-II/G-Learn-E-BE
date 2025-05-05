package gnu.capstone.G_Learn_E.domain.workbook.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record Content(
        String summaryText,
        MultipartFile pdfFile,
        MultipartFile audioFile
) {
    public static Content of(String summaryText, MultipartFile pdfFile, MultipartFile audioFile) {
        return new Content(summaryText, pdfFile, audioFile);
    }
}