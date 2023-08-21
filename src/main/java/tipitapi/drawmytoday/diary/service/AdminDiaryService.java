package tipitapi.drawmytoday.diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.admin.dto.GetDiaryAdminResponse;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.r2.service.R2PreSignedService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminDiaryService {

    private final DiaryRepository diaryRepository;
    private final R2PreSignedService r2PreSignedService;
    @Value("${presigned-image.expiration.admin-diaries}")
    private int imageExpiration;

    public Page<GetDiaryAdminResponse> getDiaries(int size, int page, Direction direction,
        Long emotionId) {
        return diaryRepository.getDiariesForMonitorAsPage(
                Pageable.ofSize(size).withPage(page), direction, emotionId)
            .map(this::generatePresignedURL);
    }

    private GetDiaryAdminResponse generatePresignedURL(GetDiaryAdminResponse response) {
        response.updateImageUrl(
            r2PreSignedService.getPreSignedUrlForShare(response.getImageURL(), imageExpiration));
        return response;
    }
}
