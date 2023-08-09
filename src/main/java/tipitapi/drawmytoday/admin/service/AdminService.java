package tipitapi.drawmytoday.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.admin.dto.GetDiaryAdminResponse;
import tipitapi.drawmytoday.diary.service.AdminDiaryService;
import tipitapi.drawmytoday.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {

    private final ValidateUserService validateUserService;
    private final AdminDiaryService adminDiaryService;

    public Page<GetDiaryAdminResponse> getDiaries(Long userId, int size, int page,
        Direction direction, Long emotionId) {
        validateUserService.validateAdminUserById(userId);
        return adminDiaryService.getDiaries(size, page, direction, emotionId);
    }
}
