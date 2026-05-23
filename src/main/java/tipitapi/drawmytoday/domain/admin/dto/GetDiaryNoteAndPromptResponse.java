package tipitapi.drawmytoday.domain.admin.dto;

import lombok.Getter;

@Getter
public class GetDiaryNoteAndPromptResponse {

    private final String promptId;
    private String notes;
    private final String prompt;

    public GetDiaryNoteAndPromptResponse(String promptId, String notes, String prompt) {
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
