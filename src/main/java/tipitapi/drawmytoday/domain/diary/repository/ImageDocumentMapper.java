package tipitapi.drawmytoday.domain.diary.repository;

import com.google.cloud.firestore.DocumentSnapshot;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import tipitapi.drawmytoday.common.util.FirestoreIdUtils;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.domain.Image;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;

@Component
public class ImageDocumentMapper {

    static final String FIELD_IMAGE_ID = "imageId";
    static final String FIELD_IMAGE_URL = "imageUrl";
    static final String FIELD_IS_SELECTED = "isSelected";
    static final String FIELD_REVIEW = "review";
    static final String FIELD_CREATED_AT = "createdAt";
    static final String FIELD_DELETED_AT = "deletedAt";
    static final String FIELD_PROMPT = "prompt";

    private final PromptDocumentMapper promptMapper;

    public ImageDocumentMapper(PromptDocumentMapper promptMapper) {
        this.promptMapper = promptMapper;
    }

    public Map<String, Object> toDocument(Image image) {
        var doc = new HashMap<String, Object>();
        doc.put(FIELD_IMAGE_ID, FirestoreIdUtils.toStorageType(image.getImageId()));
        doc.put(FIELD_IMAGE_URL, image.getImageUrl());
        doc.put(FIELD_IS_SELECTED, image.isSelected());
        doc.put(FIELD_REVIEW, image.getReview());
        doc.put(FIELD_CREATED_AT, promptMapper.toTimestamp(image.getCreatedAt()));
        doc.put(FIELD_DELETED_AT, promptMapper.toTimestamp(image.getDeletedAt()));
        doc.put(FIELD_PROMPT, promptMapper.toEmbeddedDocument(image.getPrompt()));
        return doc;
    }

    public Image fromDocument(DocumentSnapshot snapshot) {
        String diaryId = snapshot.getReference().getParent().getParent() != null
            ? snapshot.getReference().getParent().getParent().getId()
            : null;
        Diary diary = Diary.restore(diaryId, null, null, null, null, false, null, null, null,
            null, false, null, null);
        return fromDocument(snapshot, diary);
    }

    public Image fromDocument(DocumentSnapshot snapshot, Diary diary) {
        String imageId = FirestoreIdUtils.toDomainId(snapshot.get(FIELD_IMAGE_ID), snapshot.getId());
        Prompt prompt = promptMapper.fromEmbeddedDocument(snapshot.get(FIELD_PROMPT));
        String imageUrl = snapshot.getString(FIELD_IMAGE_URL);
        Boolean isSelected = snapshot.getBoolean(FIELD_IS_SELECTED);
        String review = snapshot.getString(FIELD_REVIEW);
        LocalDateTime createdAt = promptMapper.toLocalDateTime(snapshot.get(FIELD_CREATED_AT));
        LocalDateTime deletedAt = promptMapper.toLocalDateTime(snapshot.get(FIELD_DELETED_AT));
        return Image.restore(imageId, diary, prompt, imageUrl, Boolean.TRUE.equals(isSelected),
            review, deletedAt, createdAt);
    }

    public Map<String, Object> toSelectedImageDocument(Image image) {
        if (image == null) {
            return null;
        }
        var doc = new HashMap<String, Object>();
        doc.put(FIELD_IMAGE_ID, FirestoreIdUtils.toStorageType(image.getImageId()));
        doc.put(FIELD_IMAGE_URL, image.getImageUrl());
        doc.put(FIELD_REVIEW, image.getReview());
        Prompt prompt = image.getPrompt();
        doc.put(PromptDocumentMapper.FIELD_PROMPT_ID, prompt != null ? FirestoreIdUtils.toStorageType(prompt.getPromptId()) : null);
        doc.put(PromptDocumentMapper.FIELD_PROMPT_TEXT,
            prompt != null ? prompt.getPromptText() : null);
        if (prompt != null && prompt.getPromptGeneratorResult() != null
            && prompt.getPromptGeneratorResult().getPromptGeneratorType() != null) {
            doc.put(PromptDocumentMapper.FIELD_PROMPT_GENERATOR_TYPE,
                prompt.getPromptGeneratorResult().getPromptGeneratorType().name());
        } else {
            doc.put(PromptDocumentMapper.FIELD_PROMPT_GENERATOR_TYPE, null);
        }
        return doc;
    }
}
