package tipitapi.drawmytoday.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.admin.dto.GetDiaryAdminResponse;
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
        return adminDiaryService.getDiaries(size, page, direction).map(this::buildImageUrl);
    }

    private GetDiaryAdminResponse buildImageUrl(GetDiaryAdminResponse response) {
        String imageUrl = s3PreSignedService.getPreSignedUrlForShare(response.getImageURL(),
            getDiariesExpiration);
        response.updateImageUrl(imageUrl);
        return response;
    }
}
