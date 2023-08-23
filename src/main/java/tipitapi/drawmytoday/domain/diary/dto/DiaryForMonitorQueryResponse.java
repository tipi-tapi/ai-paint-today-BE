package tipitapi.drawmytoday.domain.diary.dto;

public interface DiaryForMonitorQueryResponse {

    Long getId();

    String getImageUrl();

    String getPrompt();

    String getCreatedAt();
}
