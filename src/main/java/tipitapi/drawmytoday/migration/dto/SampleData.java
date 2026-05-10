package tipitapi.drawmytoday.migration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class SampleData {

    private SampleMetadata metadata;
    private List<SampleUser> users;
    private List<SampleEmotion> emotions;
    private List<SampleDiary> diaries;
    private List<SamplePrompt> prompts;
    private List<SampleImage> images;
    private List<SampleTicket> tickets;

    @Getter
    @NoArgsConstructor
    public static class SampleMetadata {
        @JsonProperty("extractedAt")
        private String extractedAt;
        @JsonProperty("sampleUserCount")
        private Integer sampleUserCount;
        @JsonProperty("totalCounts")
        private Map<String, Object> totalCounts;
    }

    @Getter
    @NoArgsConstructor
    public static class SampleUser {
        @JsonProperty("user_id")
        private Long userId;
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
        @JsonProperty("updated_at")
        private LocalDateTime updatedAt;
        @JsonProperty("email")
        private String email;
        @JsonProperty("last_diary_date")
        private LocalDateTime lastDiaryDate;
        @JsonProperty("social_code")
        private String socialCode;
        @JsonProperty("user_role")
        private String userRole;
        @JsonProperty("deleted_at")
        private LocalDateTime deletedAt;
    }

    @Getter
    @NoArgsConstructor
    public static class SampleEmotion {
        @JsonProperty("emotion_id")
        private Long emotionId;
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
        @JsonProperty("color")
        private String color;
        @JsonProperty("color_prompt")
        private String colorPrompt;
        @JsonProperty("emotion_prompt")
        private String emotionPrompt;
        @JsonProperty("is_active")
        private Boolean isActive;
        @JsonProperty("name")
        private String name;
    }

    @Getter
    @NoArgsConstructor
    public static class SampleDiary {
        @JsonProperty("diary_id")
        private Long diaryId;
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
        @JsonProperty("updated_at")
        private LocalDateTime updatedAt;
        @JsonProperty("deleted_at")
        private LocalDateTime deletedAt;
        @JsonProperty("diary_date")
        private LocalDateTime diaryDate;
        @JsonProperty("is_ai")
        private Boolean isAi;
        @JsonProperty("notes")
        private String notes;
        @JsonProperty("title")
        private String title;
        @JsonProperty("weather")
        private String weather;
        @JsonProperty("emotion_id")
        private Long emotionId;
        @JsonProperty("user_id")
        private Long userId;
        @JsonProperty("is_test")
        private Boolean isTest;
    }

    @Getter
    @NoArgsConstructor
    public static class SamplePrompt {
        @JsonProperty("prompt_id")
        private Long promptId;
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
        @JsonProperty("is_success")
        private Boolean isSuccess;
        @JsonProperty("prompt_text")
        private String promptText;
        @JsonProperty("prompt_generator_type")
        private String promptGeneratorType;
        @JsonProperty("prompt_generator_content")
        private String promptGeneratorContent;
    }

    @Getter
    @NoArgsConstructor
    public static class SampleImage {
        @JsonProperty("image_id")
        private Long imageId;
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
        @JsonProperty("deleted_at")
        private LocalDateTime deletedAt;
        @JsonProperty("image_url")
        private String imageUrl;
        @JsonProperty("is_selected")
        private Boolean isSelected;
        @JsonProperty("diary_id")
        private Long diaryId;
        @JsonProperty("review")
        private String review;
        @JsonProperty("prompt_id")
        private Long promptId;
    }

    @Getter
    @NoArgsConstructor
    public static class SampleTicket {
        @JsonProperty("ticket_id")
        private Long ticketId;
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
        @JsonProperty("ticket_type")
        private String ticketType;
        @JsonProperty("used_at")
        private LocalDateTime usedAt;
        @JsonProperty("user_id")
        private Long userId;
    }

}
