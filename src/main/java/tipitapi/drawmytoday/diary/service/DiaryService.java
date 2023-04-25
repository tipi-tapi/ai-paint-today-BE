package tipitapi.drawmytoday.diary.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.diary.dto.DiaryResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {
//  private final DiaryRepository diaryRepository;

  public DiaryResponse getDiary(Long diaryId) {
    return new DiaryResponse(diaryId, "", LocalDateTime.now(), LocalDateTime.now(), "", new ArrayList<>());
  }
}
