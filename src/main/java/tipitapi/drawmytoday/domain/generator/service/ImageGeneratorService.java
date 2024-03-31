package tipitapi.drawmytoday.domain.generator.service;

import java.util.List;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest;
import tipitapi.drawmytoday.domain.generator.exception.ImageGeneratorException;

public interface ImageGeneratorService {

    byte[] generateImage(Prompt prompt) throws ImageGeneratorException;

    List<byte[]> generateTestImage(CreateTestDiaryRequest request) throws ImageGeneratorException;
}
