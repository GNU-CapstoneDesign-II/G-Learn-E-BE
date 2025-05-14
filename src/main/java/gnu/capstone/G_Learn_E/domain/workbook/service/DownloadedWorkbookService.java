package gnu.capstone.G_Learn_E.domain.workbook.service;

import gnu.capstone.G_Learn_E.domain.workbook.repository.DownloadedWorkbookMapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DownloadedWorkbookService {

    private final DownloadedWorkbookMapRepository downloadedWorkbookMapRepository;

    public boolean isWorkbookDownloaded(Long userId, Long workbookId) {
        return downloadedWorkbookMapRepository.existsByUserIdAndWorkbookId(userId, workbookId);
    }

    public Set<Long> getUsersDownloadedWorkbookIds(Long userId) {
        return downloadedWorkbookMapRepository.findAllByUserId(userId)
                .stream()
                .map(downloadedWorkbookMap -> downloadedWorkbookMap.getWorkbook().getId())
                .collect(Collectors.toSet());
    }
}
