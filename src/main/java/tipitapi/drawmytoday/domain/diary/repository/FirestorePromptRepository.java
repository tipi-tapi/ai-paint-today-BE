package tipitapi.drawmytoday.domain.diary.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import tipitapi.drawmytoday.common.util.FirestoreIdUtils;
import tipitapi.drawmytoday.common.util.IdGenerator;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FirestorePromptRepository implements PromptRepository {

    private static final String IMAGES_COLLECTION = "images";
    private static final long TIMEOUT_SECONDS = 5L;

    private final Firestore firestore;
    private final PromptDocumentMapper mapper;
    private final ImageDocumentMapper imageMapper;

    @Override
    public Prompt save(Prompt prompt) {
        Prompt target = restoreForSave(prompt);
        try {
            var images = firestore.collectionGroup(IMAGES_COLLECTION)
                .whereEqualTo("prompt.promptId", FirestoreIdUtils.toStorageType(target.getPromptId()))
                .get()
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .getDocuments();
            for (var image : images) {
                image.getReference()
                    .set(java.util.Map.of(ImageDocumentMapper.FIELD_PROMPT,
                        mapper.toEmbeddedDocument(target)), SetOptions.merge())
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                if (Boolean.TRUE.equals(image.getBoolean(ImageDocumentMapper.FIELD_IS_SELECTED))
                    && image.getReference().getParent().getParent() != null) {
                    var updatedImage = ImageDocumentMapperSnapshot.updatePrompt(imageMapper, image, target);
                    image.getReference().getParent().getParent()
                        .set(java.util.Map.of(DiaryDocumentMapper.FIELD_SELECTED_IMAGE,
                            imageMapper.toSelectedImageDocument(updatedImage)), SetOptions.merge())
                        .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                }
            }
            return target;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        } catch (TimeoutException e) {
            throw new BusinessException(ErrorCode.FIRESTORE_TIMEOUT, e);
        } catch (ExecutionException e) {
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        }
    }

    @Override
    public Optional<Prompt> findById(String promptId) {
        return findPromptByField("prompt.promptId", promptId);
    }

    @Override
    public Optional<Prompt> findByImageId(String imageId) {
        return findPromptByField(ImageDocumentMapper.FIELD_IMAGE_ID, imageId);
    }

    @Override
    public List<Prompt> findAllByDiaryDiaryIdAndIsSuccessTrue(String diaryId) {
        try {
            return firestore.collection("diaries")
                .document(diaryId)
                .collection(IMAGES_COLLECTION)
                .get()
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .getDocuments()
                .stream()
                .filter(snapshot -> snapshot.get(ImageDocumentMapper.FIELD_DELETED_AT) == null)
                .map(snapshot -> mapper.fromEmbeddedDocument(snapshot.get(ImageDocumentMapper.FIELD_PROMPT)))
                .filter(prompt -> prompt != null && prompt.isSuccess())
                .collect(Collectors.toList());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        } catch (TimeoutException e) {
            throw new BusinessException(ErrorCode.FIRESTORE_TIMEOUT, e);
        } catch (ExecutionException e) {
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        }
    }

    private Optional<Prompt> findPromptByField(String field, String value) {
        try {
            return firestore.collectionGroup(IMAGES_COLLECTION)
                .whereEqualTo(field, FirestoreIdUtils.toStorageType(value))
                .limit(1)
                .get()
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .getDocuments()
                .stream()
                .filter(snapshot -> snapshot.get(ImageDocumentMapper.FIELD_DELETED_AT) == null)
                .findFirst()
                .map(snapshot -> mapper.fromEmbeddedDocument(snapshot.get(ImageDocumentMapper.FIELD_PROMPT)));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        } catch (TimeoutException e) {
            throw new BusinessException(ErrorCode.FIRESTORE_TIMEOUT, e);
        } catch (ExecutionException e) {
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        }
    }

    private Prompt restoreForSave(Prompt prompt) {
        LocalDateTime now = LocalDateTime.now();
        return Prompt.restore(
            prompt.getPromptId() != null ? prompt.getPromptId() : generateId(),
            prompt.getPromptGeneratorResult(),
            prompt.getPromptText(),
            prompt.isSuccess(),
            prompt.getCreatedAt() != null ? prompt.getCreatedAt() : now
        );
    }

    private String generateId() {
        return IdGenerator.generate();
    }

    private static class ImageDocumentMapperSnapshot {

        static tipitapi.drawmytoday.domain.diary.domain.Image updatePrompt(
            ImageDocumentMapper imageMapper,
            com.google.cloud.firestore.DocumentSnapshot snapshot,
            Prompt prompt) {
            var image = imageMapper.fromDocument(snapshot);
            return tipitapi.drawmytoday.domain.diary.domain.Image.restore(
                image.getImageId(),
                image.getDiary(),
                prompt,
                image.getImageUrl(),
                image.isSelected(),
                image.getReview(),
                image.getDeletedAt(),
                image.getCreatedAt()
            );
        }
    }
}
