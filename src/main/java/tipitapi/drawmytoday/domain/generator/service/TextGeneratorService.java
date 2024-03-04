package tipitapi.drawmytoday.domain.generator.service;

import java.util.List;
import tipitapi.drawmytoday.domain.generator.api.gpt.domain.Message;

public interface TextGeneratorService {

    List<Message> generatePrompt(String keyword);
}
