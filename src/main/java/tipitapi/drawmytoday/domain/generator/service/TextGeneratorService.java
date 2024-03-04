package tipitapi.drawmytoday.domain.generator.service;

import java.util.List;
import tipitapi.drawmytoday.domain.generator.domain.TextGeneratorContent;

public interface TextGeneratorService {

    List<? extends TextGeneratorContent> generatePrompt(String keyword);
}
