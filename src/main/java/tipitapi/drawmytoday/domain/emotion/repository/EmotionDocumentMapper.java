package tipitapi.drawmytoday.domain.emotion.repository;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import org.springframework.stereotype.Component;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Component
public class EmotionDocumentMapper {

    private static final String FIELD_EMOTION_ID = "emotionId";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_COLOR = "color";
    private static final String FIELD_COLOR_PROMPT = "colorPrompt";
    private static final String FIELD_EMOTION_PROMPT = "emotionPrompt";
    private static final String FIELD_IS_ACTIVE = "isActive";
    private static final String FIELD_CREATED_AT = "createdAt";

    public Map<String, Object> toDocument(Emotion emotion) {
        var doc = new HashMap<String, Object>();
        if (emotion.getEmotionId() != null) {
            doc.put(FIELD_EMOTION_ID, String.valueOf(emotion.getEmotionId()));
        }
        doc.put(FIELD_NAME, emotion.getName());
        doc.put(FIELD_COLOR, emotion.getColor());
        doc.put(FIELD_COLOR_PROMPT, emotion.getColorPrompt());
        doc.put(FIELD_EMOTION_PROMPT, emotion.getEmotionPrompt());
        doc.put(FIELD_IS_ACTIVE, emotion.isActive());
        if (emotion.getCreatedAt() != null) {
            doc.put(FIELD_CREATED_AT, toTimestamp(emotion.getCreatedAt()));
        }
        return doc;
    }

    public Emotion fromDocument(DocumentSnapshot snapshot) {
        Long emotionId = Long.parseLong(snapshot.getId());
        String name = snapshot.getString(FIELD_NAME);
        String color = snapshot.getString(FIELD_COLOR);
        String colorPrompt = snapshot.getString(FIELD_COLOR_PROMPT);
        String emotionPrompt = snapshot.getString(FIELD_EMOTION_PROMPT);
        Boolean isActive = snapshot.getBoolean(FIELD_IS_ACTIVE);
        LocalDateTime createdAt = toLocalDateTime(snapshot.getTimestamp(FIELD_CREATED_AT));

        return Emotion.restore(
            emotionId, name, color,
            Boolean.TRUE.equals(isActive),
            emotionPrompt, colorPrompt, createdAt
        );
    }

    private Timestamp toTimestamp(LocalDateTime ldt) {
        if (ldt == null) return null;
        var instant = ldt.atZone(ZoneId.systemDefault()).toInstant();
        return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) return null;
        return timestamp.toDate().toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }
}
