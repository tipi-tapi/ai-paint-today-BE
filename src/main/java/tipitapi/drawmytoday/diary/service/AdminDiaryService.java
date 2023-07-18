package tipitapi.drawmytoday.diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.diary.dto.DiaryForMonitorQueryResponse;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminDiaryService {

    private final DiaryRepository diaryRepository;

    @Value("${dummy.image.path}")
    private String DUMMY_IMAGE_PATH;

    public Page<DiaryForMonitorQueryResponse> getDiaries(int size, int page,
        Direction direction) {
        return diaryRepository.getAllDiariesForMonitorAsPage(
            PageRequest.of(page, size, Sort.by(direction, "created_at", "diary_id")),
            DUMMY_IMAGE_PATH);
    }
}
