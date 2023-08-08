package tipitapi.drawmytoday.diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.admin.dto.GetDiaryAdminResponse;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminDiaryService {

    private final DiaryRepository diaryRepository;

    public Page<GetDiaryAdminResponse> getDiaries(int size, int page, Direction direction) {
        return diaryRepository.getDiariesForMonitorAsPage(
            Pageable.ofSize(size).withPage(page), direction);
    }
}
