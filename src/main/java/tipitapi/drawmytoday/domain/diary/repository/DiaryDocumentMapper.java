package tipitapi.drawmytoday.domain.diary.repository;

import com.google.cloud.firestore.DocumentSnapshot;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.user.domain.User;

@Component
public class DiaryDocumentMapper {

    static final String FIELD_DIARY_ID = "diaryId";
    static final String FIELD_USER_ID = "userId";
    static final String FIELD_DIARY_DATE = "diaryDate";
    static final String FIELD_IS_AI = "isAi";
    static final String FIELD_NOTES = "notes";
    static final String FIELD_TITLE = "title";
    static final String FIELD_WEATHER = "weather";
    static final String FIELD_IS_TEST = "isTest";
    static final String FIELD_EMOTION = "emotion";
    static final String FIELD_SELECTED_IMAGE = "selectedImage";
    static final String FIELD_IMAGE_COUNT = "imageCount";
    static final String FIELD_CREATED_AT = "createdAt";
    static final String FIELD_UPDATED_AT = "updatedAt";
    static final String FIELD_DELETED_AT = "deletedAt";

    private static final String FIELD_EMOTION_ID = "emotionId";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_COLOR = "color";
    private static final String FIELD_COLOR_PROMPT = "colorPrompt";
    private static final String FIELD_EMOTION_PROMPT = "emotionPrompt";

    private final PromptDocumentMapper timeMapper;

    public DiaryDocumentMapper(PromptDocumentMapper timeMapper) {
        this.timeMapper = timeMapper;
    }

    public Map<String, Object> toDocument(Diary diary, Object selectedImage, long imageCount) {
        var doc = new HashMap<String, Object>();
        doc.put(FIELD_DIARY_ID, diary.getDiaryId());
        doc.put(FIELD_USER_ID, diary.getUser() != null ? diary.getUser().getUserId() : null);
        doc.put(FIELD_DIARY_DATE, timeMapper.toTimestamp(diary.getDiaryDate()));
        doc.put(FIELD_IS_AI, diary.isAi());
        doc.put(FIELD_NOTES, diary.getNotes());
        doc.put(FIELD_TITLE, diary.getTitle());
        doc.put(FIELD_WEATHER, diary.getWeather());
        doc.put(FIELD_IS_TEST, diary.isTest());
        doc.put(FIELD_EMOTION, toEmotionDocument(diary.getEmotion()));
        doc.put(FIELD_SELECTED_IMAGE, selectedImage);
        doc.put(FIELD_IMAGE_COUNT, imageCount);
        doc.put(FIELD_CREATED_AT, timeMapper.toTimestamp(diary.getCreatedAt()));
        doc.put(FIELD_UPDATED_AT, timeMapper.toTimestamp(diary.getUpdatedAt()));
        doc.put(FIELD_DELETED_AT, timeMapper.toTimestamp(diary.getDeletedAt()));
        return doc;
    }

    public Diary fromDocument(DocumentSnapshot snapshot) {
        Long diaryId = timeMapper.toLong(snapshot.get(FIELD_DIARY_ID), snapshot.getId());
        User user = toUser(snapshot.get(FIELD_USER_ID));
        Emotion emotion = toEmotion(snapshot.get(FIELD_EMOTION));
        LocalDateTime diaryDate = timeMapper.toLocalDateTime(snapshot.get(FIELD_DIARY_DATE));
        LocalDateTime createdAt = timeMapper.toLocalDateTime(snapshot.get(FIELD_CREATED_AT));
        LocalDateTime updatedAt = timeMapper.toLocalDateTime(snapshot.get(FIELD_UPDATED_AT));
        LocalDateTime deletedAt = timeMapper.toLocalDateTime(snapshot.get(FIELD_DELETED_AT));
        return Diary.restore(
            diaryId,
            user,
            emotion,
            diaryDate,
            snapshot.getString(FIELD_NOTES),
            Boolean.TRUE.equals(snapshot.getBoolean(FIELD_IS_AI)),
            snapshot.getString(FIELD_TITLE),
            snapshot.getString(FIELD_WEATHER),
            null,
            deletedAt,
            Boolean.TRUE.equals(snapshot.getBoolean(FIELD_IS_TEST)),
            createdAt,
            updatedAt
        );
    }

    private Map<String, Object> toEmotionDocument(Emotion emotion) {
        if (emotion == null) {
            return null;
        }
        var doc = new HashMap<String, Object>();
        doc.put(FIELD_EMOTION_ID, emotion.getEmotionId());
        doc.put(FIELD_NAME, emotion.getName());
        doc.put(FIELD_COLOR, emotion.getColor());
        doc.put(FIELD_COLOR_PROMPT, emotion.getColorPrompt());
        doc.put(FIELD_EMOTION_PROMPT, emotion.getEmotionPrompt());
        return doc;
    }

    private Emotion toEmotion(Object value) {
        if (!(value instanceof Map)) {
            return null;
        }
        Map<?, ?> doc = (Map<?, ?>) value;
        Long emotionId = timeMapper.toLong(doc.get(FIELD_EMOTION_ID), null);
        return Emotion.restore(
            emotionId,
            timeMapper.toString(doc.get(FIELD_NAME)),
            timeMapper.toString(doc.get(FIELD_COLOR)),
            true,
            timeMapper.toString(doc.get(FIELD_EMOTION_PROMPT)),
            timeMapper.toString(doc.get(FIELD_COLOR_PROMPT)),
            null
        );
    }

    private User toUser(Object userId) {
        Long parsed = timeMapper.toLong(userId, null);
        if (parsed == null) {
            return null;
        }
        return User.restore(parsed, null, null, null, null, null, null, null);
    }
}
