package tipitapi.drawmytoday.domain.admin.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class GetDiaryNoteAndPromptResponse {

    private final Long promptId;
    private String notes;
    private final String prompt;

    @QueryProjection
    public GetDiaryNoteAndPromptResponse(Long promptId, String notes, String prompt) {
        this.promptId = promptId;
        this.notes = notes;
        this.prompt = prompt;
    }

    public void updateNotes(String notes) {
        this.notes = notes;
    }

    public String getGptPrompt() {
        String[] promptTexts = prompt.split("Impressionist oil painting,");
        return promptTexts[promptTexts.length - 1].trim();
    }
}
