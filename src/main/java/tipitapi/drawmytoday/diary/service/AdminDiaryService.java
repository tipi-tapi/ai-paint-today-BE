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
import tipitapi.drawmytoday.s3.service.S3PreSignedService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminDiaryService {

    private final DiaryRepository diaryRepository;
    private final S3PreSignedService s3PreSignedService;
    @Value("${presigned-image.expiration.admin-diaries}")
    private int imageExpiration;

    public Page<GetDiaryAdminResponse> getDiaries(int size, int page, Direction direction) {
        return diaryRepository.getDiariesForMonitorAsPage(
            Pageable.ofSize(size).withPage(page), direction).map(this::generatePresignedURL);
    }

    private GetDiaryAdminResponse generatePresignedURL(GetDiaryAdminResponse response) {
        response.updateImageUrl(
            s3PreSignedService.getPreSignedUrlForShare(response.getImageURL(), imageExpiration));
        return response;
    }
}
