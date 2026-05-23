package tipitapi.drawmytoday.domain.diary.repository;

import java.util.List;
import java.util.Optional;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;

public interface PromptRepository {

    Prompt save(Prompt prompt);

    Optional<Prompt> findById(String promptId);

    Optional<Prompt> findByImageId(String imageId);

    List<Prompt> findAllByDiaryDiaryIdAndIsSuccessTrue(String diaryId);
}
