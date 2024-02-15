package tipitapi.drawmytoday.domain.generator.domain.gpt.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.domain.generator.domain.gpt.domain.Message;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChoiceResponse {

    private String firstReason;
    private String index;
    private Message message;
    

}
