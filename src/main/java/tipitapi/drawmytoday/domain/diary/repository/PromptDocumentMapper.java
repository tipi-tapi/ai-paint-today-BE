package tipitapi.drawmytoday.domain.diary.repository;

import com.google.cloud.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import tipitapi.drawmytoday.common.util.FirestoreIdUtils;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.domain.PromptGeneratorResult;
import tipitapi.drawmytoday.domain.diary.domain.PromptGeneratorType;

@Component
public class PromptDocumentMapper {

    static final String FIELD_PROMPT_ID = "promptId";
    static final String FIELD_PROMPT_TEXT = "promptText";
    static final String FIELD_IS_SUCCESS = "isSuccess";
    static final String FIELD_PROMPT_GENERATOR_TYPE = "promptGeneratorType";
    static final String FIELD_PROMPT_GENERATOR_CONTENT = "promptGeneratorContent";
    static final String FIELD_PROMPT_CREATED_AT = "promptCreatedAt";

    public Map<String, Object> toEmbeddedDocument(Prompt prompt) {
        if (prompt == null) {
            return null;
        }
        var doc = new HashMap<String, Object>();
        doc.put(FIELD_PROMPT_ID, FirestoreIdUtils.toStorageType(prompt.getPromptId()));
        doc.put(FIELD_PROMPT_TEXT, prompt.getPromptText());
        doc.put(FIELD_IS_SUCCESS, prompt.isSuccess());
        PromptGeneratorResult result = prompt.getPromptGeneratorResult();
        doc.put(FIELD_PROMPT_GENERATOR_TYPE,
            result != null && result.getPromptGeneratorType() != null
                ? result.getPromptGeneratorType().name()
                : null);
        doc.put(FIELD_PROMPT_GENERATOR_CONTENT,
            result != null ? result.getPromptGeneratorContent() : null);
        doc.put(FIELD_PROMPT_CREATED_AT, toTimestamp(prompt.getCreatedAt()));
        return doc;
    }

    public Prompt fromEmbeddedDocument(Object value) {
        if (!(value instanceof Map)) {
            return null;
        }
        Map<?, ?> doc = (Map<?, ?>) value;
        String promptId = FirestoreIdUtils.toDomainId(doc.get(FIELD_PROMPT_ID), null);
        String promptText = toString(doc.get(FIELD_PROMPT_TEXT));
        boolean isSuccess = Boolean.TRUE.equals(doc.get(FIELD_IS_SUCCESS));
        String type = toString(doc.get(FIELD_PROMPT_GENERATOR_TYPE));
        PromptGeneratorType promptGeneratorType = type != null
            ? PromptGeneratorType.valueOf(type)
            : PromptGeneratorType.NONE;
        PromptGeneratorResult result = PromptGeneratorResult.restore(
            promptGeneratorType,
            toString(doc.get(FIELD_PROMPT_GENERATOR_CONTENT))
        );
        LocalDateTime createdAt = toLocalDateTime(doc.get(FIELD_PROMPT_CREATED_AT));
        return Prompt.restore(promptId, result, promptText, isSuccess, createdAt);
    }

    Timestamp toTimestamp(LocalDateTime ldt) {
        if (ldt == null) {
            return null;
        }
        var instant = ldt.atZone(ZoneId.systemDefault()).toInstant();
        return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
    }

    LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Timestamp) {
            return ((Timestamp) value).toDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        }
        if (value instanceof Date) {
            return ((Date) value).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        }
        return null;
    }

    String toString(Object value) {
        return value != null ? String.valueOf(value) : null;
    }
}
