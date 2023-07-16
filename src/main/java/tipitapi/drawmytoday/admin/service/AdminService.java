package tipitapi.drawmytoday.admin.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.admin.dto.GetDiaryAdminResponse;
import tipitapi.drawmytoday.diary.dto.DiaryForMonitorQueryResponse;
import tipitapi.drawmytoday.diary.service.AdminDiaryService;
import tipitapi.drawmytoday.s3.service.S3PreSignedService;
import tipitapi.drawmytoday.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {

    private final ValidateUserService validateUserService;
    private final AdminDiaryService adminDiaryService;
    private final S3PreSignedService s3PreSignedService;
    @Value("${presigned-image.expiration.admin-diaries}")
    private int getDiariesExpiration;

    public Page<GetDiaryAdminResponse> getDiaries(Long userId, int size, int page,
        Direction direction) {
        validateUserService.validateAdminUserById(userId);
        return adminDiaryService.getDiaries(size, page, direction)
            .map(this::buildGetDiaryAdminResponse);
    }

    private GetDiaryAdminResponse buildGetDiaryAdminResponse(
        DiaryForMonitorQueryResponse response) {
        LocalDateTime createdAt = LocalDateTime.parse(response.getCreatedAt(),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.n"));
        String imageUrl = s3PreSignedService.getPreSignedUrlForShare(response.getImageUrl(),
            getDiariesExpiration);
        return GetDiaryAdminResponse.of(response.getId(), imageUrl, response.getPrompt(),
            createdAt);
    }
}
