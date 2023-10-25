package tipitapi.drawmytoday.domain.generator.service;

import java.util.List;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.generator.dto.GeneratedImageAndPrompt;
import tipitapi.drawmytoday.domain.generator.exception.ImageGeneratorException;

public interface ImageGeneratorService {

    GeneratedImageAndPrompt generateImage(Emotion emotion, String keyword)
        throws ImageGeneratorException;

    GeneratedImageAndPrompt generateImage(Prompt prompt) throws ImageGeneratorException;

    List<byte[]> generateTestImage(CreateTestDiaryRequest request) throws ImageGeneratorException;
}
