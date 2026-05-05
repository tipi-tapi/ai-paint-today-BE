package tipitapi.drawmytoday.domain.diary.repository;

import java.util.List;
import java.util.Optional;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;

public interface PromptRepository {

    Prompt save(Prompt prompt);

    Optional<Prompt> findById(Long promptId);

    Optional<Prompt> findByImageId(Long imageId);

    List<Prompt> findAllByDiaryDiaryIdAndIsSuccessTrue(Long diaryId);
}
