package tipitapi.drawmytoday.domain.generator.service;

import java.util.List;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.generator.domain.TextGeneratorContent;

public interface TextGeneratorService {

    List<? extends TextGeneratorContent> generatePrompt(String keyword, int maxLength);

    List<? extends TextGeneratorContent> regeneratePrompt(String diaryNote, Prompt prompt);
}
