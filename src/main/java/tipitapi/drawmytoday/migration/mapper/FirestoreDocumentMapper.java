package tipitapi.drawmytoday.migration.mapper;

import com.google.cloud.Timestamp;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import tipitapi.drawmytoday.migration.dto.SampleData.SampleAdReward;
import tipitapi.drawmytoday.migration.dto.SampleData.SampleAuth;
import tipitapi.drawmytoday.migration.dto.SampleData.SampleDiary;
import tipitapi.drawmytoday.migration.dto.SampleData.SampleEmotion;
import tipitapi.drawmytoday.migration.dto.SampleData.SampleImage;
import tipitapi.drawmytoday.migration.dto.SampleData.SamplePrompt;
import tipitapi.drawmytoday.migration.dto.SampleData.SampleTicket;
import tipitapi.drawmytoday.migration.dto.SampleData.SampleUser;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Profile("bulk-load")
public class FirestoreDocumentMapper {

    // KST — matches MySQL RDS timezone. systemDefault() is unsafe in containers (typically UTC).
    private static final ZoneId MYSQL_ZONE = ZoneId.of("Asia/Seoul");

    public Map<String, Object> toUserDoc(SampleUser u) {
        var doc = new HashMap<String, Object>();
        doc.put("userId", u.getUserId());
        doc.put("email", u.getEmail());
        doc.put("socialCode", u.getSocialCode());
        doc.put("userRole", u.getUserRole());
        doc.put("lastDiaryDate", toTimestamp(u.getLastDiaryDate()));
        doc.put("createdAt", toTimestamp(u.getCreatedAt()));
        doc.put("updatedAt", toTimestamp(u.getUpdatedAt()));
        doc.put("deletedAt", toTimestamp(u.getDeletedAt()));
        return doc;
    }

    public Map<String, Object> toAuthDoc(SampleAuth a) {
        var doc = new HashMap<String, Object>();
        doc.put("authId", a.getAuthId());
        doc.put("userId", a.getUserId());
        doc.put("refreshToken", a.getRefreshToken());
        doc.put("createdAt", toTimestamp(a.getCreatedAt()));
        return doc;
    }

    public Map<String, Object> toEmotionDoc(SampleEmotion e) {
        var doc = new HashMap<String, Object>();
        doc.put("emotionId", e.getEmotionId());
        doc.put("name", e.getName());
        doc.put("color", e.getColor());
        doc.put("colorPrompt", e.getColorPrompt());
        doc.put("emotionPrompt", e.getEmotionPrompt());
        doc.put("isActive", e.getIsActive());
        doc.put("createdAt", toTimestamp(e.getCreatedAt()));
        return doc;
    }

    public Map<String, Object> toDiaryDoc(
            SampleDiary d,
            SampleEmotion emotion,
            List<SampleImage> images,
            Map<Long, SamplePrompt> promptMap) {
        var selectedImage = images.stream()
            .filter(img -> Boolean.TRUE.equals(img.getIsSelected()))
            .findFirst()
            .orElse(null);

        var doc = new HashMap<String, Object>();
        doc.put("diaryId", d.getDiaryId());
        doc.put("userId", d.getUserId());
        doc.put("diaryDate", toTimestamp(d.getDiaryDate()));
        doc.put("isAi", d.getIsAi());
        doc.put("notes", d.getNotes());
        doc.put("title", d.getTitle());
        doc.put("weather", d.getWeather());
        doc.put("isTest", d.getIsTest());
        doc.put("emotion", toEmotionEmbedded(emotion));
        doc.put("imageCount", images.size());
        doc.put("selectedImage", selectedImage != null
            ? toSelectedImageEmbedded(selectedImage, promptMap.get(selectedImage.getPromptId()))
            : null);
        doc.put("createdAt", toTimestamp(d.getCreatedAt()));
        doc.put("updatedAt", toTimestamp(d.getUpdatedAt()));
        doc.put("deletedAt", toTimestamp(d.getDeletedAt()));
        return doc;
    }

    public Map<String, Object> toImageDoc(SampleImage img, @Nullable SamplePrompt prompt) {
        var doc = new HashMap<String, Object>();
        doc.put("imageId", img.getImageId());
        doc.put("imageUrl", img.getImageUrl());
        doc.put("isSelected", img.getIsSelected());
        doc.put("review", img.getReview());
        doc.put("createdAt", toTimestamp(img.getCreatedAt()));
        doc.put("deletedAt", toTimestamp(img.getDeletedAt()));
        doc.put("prompt", prompt != null ? toPromptEmbedded(prompt) : null);
        return doc;
    }

    public Map<String, Object> toTicketDoc(SampleTicket t) {
        var doc = new HashMap<String, Object>();
        doc.put("ticketId", t.getTicketId());
        doc.put("ticketType", t.getTicketType());
        doc.put("usedAt", toTimestamp(t.getUsedAt()));
        doc.put("createdAt", toTimestamp(t.getCreatedAt()));
        return doc;
    }

    public Map<String, Object> toAdRewardDoc(SampleAdReward ar) {
        var doc = new HashMap<String, Object>();
        doc.put("adRewardId", ar.getAdRewardId());
        doc.put("usedAt", toTimestamp(ar.getUsedAt()));
        doc.put("createdAt", toTimestamp(ar.getCreatedAt()));
        return doc;
    }

    @Nullable
    public Timestamp toTimestamp(@Nullable LocalDateTime ldt) {
        if (ldt == null) return null;
        var instant = ldt.atZone(MYSQL_ZONE).toInstant();
        return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
    }

    private Map<String, Object> toEmotionEmbedded(SampleEmotion e) {
        var map = new HashMap<String, Object>();
        map.put("emotionId", e.getEmotionId());
        map.put("name", e.getName());
        map.put("color", e.getColor());
        map.put("colorPrompt", e.getColorPrompt());
        map.put("emotionPrompt", e.getEmotionPrompt());
        return map;
    }

    private Map<String, Object> toSelectedImageEmbedded(SampleImage img, @Nullable SamplePrompt prompt) {
        var map = new HashMap<String, Object>();
        map.put("imageId", img.getImageId());
        map.put("imageUrl", img.getImageUrl());
        map.put("review", img.getReview());
        map.put("promptId", img.getPromptId());
        map.put("promptText", prompt != null ? prompt.getPromptText() : null);
        map.put("promptGeneratorType", prompt != null ? prompt.getPromptGeneratorType() : null);
        return map;
    }

    private Map<String, Object> toPromptEmbedded(SamplePrompt p) {
        var map = new HashMap<String, Object>();
        map.put("promptId", p.getPromptId());
        map.put("promptText", p.getPromptText());
        map.put("isSuccess", p.getIsSuccess());
        map.put("promptGeneratorType", p.getPromptGeneratorType());
        map.put("promptGeneratorContent", p.getPromptGeneratorContent());
        map.put("promptCreatedAt", toTimestamp(p.getCreatedAt()));
        return map;
    }
}
