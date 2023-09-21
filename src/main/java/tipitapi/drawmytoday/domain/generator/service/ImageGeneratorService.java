package tipitapi.drawmytoday.domain.generator.service;

import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.generator.dto.GeneratedImageAndPrompt;
import tipitapi.drawmytoday.domain.generator.exception.ImageGeneratorException;

public interface ImageGeneratorService {

    GeneratedImageAndPrompt generateImage(Emotion emotion, String keyword)
        throws ImageGeneratorException;

    GeneratedImageAndPrompt generateImage(Prompt prompt) throws ImageGeneratorException;
}
