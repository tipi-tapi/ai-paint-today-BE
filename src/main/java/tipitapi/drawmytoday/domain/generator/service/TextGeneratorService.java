package tipitapi.drawmytoday.domain.generator.service;

import tipitapi.drawmytoday.domain.generator.domain.gpt.domain.Message;
import java.util.List;

public interface TextGeneratorService {

    List<Message> generatePrompt(String keyword);
}
