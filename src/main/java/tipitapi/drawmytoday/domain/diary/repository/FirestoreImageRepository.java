package tipitapi.drawmytoday.domain.diary.repository;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import java.time.LocalDateTime;
import java.util.Comparator;
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
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.domain.Image;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.user.domain.User;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FirestoreImageRepository implements ImageRepository {

    private static final String DIARIES_COLLECTION = "diaries";
    private static final String IMAGES_COLLECTION = "images";
    private static final long TIMEOUT_SECONDS = 5L;

    private final Firestore firestore;
    private final ImageDocumentMapper imageMapper;
    private final PromptDocumentMapper promptMapper;

    @Override
    public Image save(Image image) {
        try {
            String diaryId = requireDiaryId(image);
            Image target = restoreForSave(image);
            var ref = imageCollection(diaryId).document(target.getImageId());
            ref.set(imageMapper.toDocument(target)).get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            syncDiaryImageSummary(diaryId, target.isSelected() ? target : null);
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
    public void delete(Image image) {
        try {
            String diaryId = requireDiaryId(image);
            imageCollection(diaryId)
                .document(image.getImageId())
                .set(java.util.Map.of(ImageDocumentMapper.FIELD_DELETED_AT,
                    promptMapper.toTimestamp(LocalDateTime.now())), SetOptions.merge())
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            syncDiaryImageSummary(diaryId, null);
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
    public List<Image> findLatestByDiary(String diaryId) {
        return findByDiary(diaryId).stream()
            .sorted(Comparator.comparing(Image::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Image> findImage(String imageId) {
        try {
            return firestore.collectionGroup(IMAGES_COLLECTION)
                .whereEqualTo(ImageDocumentMapper.FIELD_IMAGE_ID, FirestoreIdUtils.toStorageType(imageId))
                .limit(1)
                .get()
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .getDocuments()
                .stream()
                .filter(this::isLiveImage)
                .findFirst()
                .map(imageMapper::fromDocument);
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
    public Long countImage(String diaryId) {
        return (long) findByDiary(diaryId).size();
    }

    @Override
    public List<Image> findByDiary(String diaryId) {
        try {
            Diary diary = Diary.restore(diaryId, null, null, null, null, false, null, null, null,
                null, false, null, null);
            return imageCollection(diaryId)
                .get()
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .getDocuments()
                .stream()
                .filter(this::isLiveImage)
                .map(snapshot -> imageMapper.fromDocument(snapshot, diary))
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

    @Override
    public Optional<Image> findByImageIdAndDiaryUser(String imageId, User user) {
        return findImage(imageId)
            .filter(image -> image.getDiary() != null
                && image.getDiary().getDiaryId() != null
                && diaryOwnedBy(image.getDiary().getDiaryId(), user));
    }

    @Override
    public Optional<Image> findRecentByDiary(String diaryId) {
        return findLatestByDiary(diaryId).stream().findFirst();
    }

    private void syncDiaryImageSummary(String diaryId, Image selectedImage) throws InterruptedException, ExecutionException, TimeoutException {
        long count = imageCollection(diaryId)
            .get()
            .get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .getDocuments()
            .stream()
            .filter(this::isLiveImage)
            .count();
        var update = new java.util.HashMap<String, Object>();
        update.put(DiaryDocumentMapper.FIELD_IMAGE_COUNT, count);
        if (selectedImage != null) {
            update.put(DiaryDocumentMapper.FIELD_SELECTED_IMAGE,
                imageMapper.toSelectedImageDocument(selectedImage));
        }
        firestore.collection(DIARIES_COLLECTION)
            .document(diaryId)
            .set(update, SetOptions.merge())
            .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private Image restoreForSave(Image image) {
        LocalDateTime now = LocalDateTime.now();
        Prompt prompt = restorePromptForSave(image.getPrompt(), now);
        return Image.restore(
            image.getImageId() != null ? image.getImageId() : generateId(),
            image.getDiary(),
            prompt,
            image.getImageUrl(),
            image.isSelected(),
            image.getReview(),
            image.getDeletedAt(),
            image.getCreatedAt() != null ? image.getCreatedAt() : now
        );
    }

    private Prompt restorePromptForSave(Prompt prompt, LocalDateTime now) {
        if (prompt == null) {
            return null;
        }
        return Prompt.restore(
            prompt.getPromptId() != null ? prompt.getPromptId() : generateId(),
            prompt.getPromptGeneratorResult(),
            prompt.getPromptText(),
            prompt.isSuccess(),
            prompt.getCreatedAt() != null ? prompt.getCreatedAt() : now
        );
    }

    private boolean diaryOwnedBy(String diaryId, User user) {
        try {
            var diary = firestore.collection(DIARIES_COLLECTION)
                .document(diaryId)
                .get()
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!diary.exists() || user == null || user.getUserId() == null) {
                return false;
            }
            String storedId = FirestoreIdUtils.toDomainId(diary.get(DiaryDocumentMapper.FIELD_USER_ID), null);
            return user.getUserId().equals(storedId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        } catch (TimeoutException e) {
            throw new BusinessException(ErrorCode.FIRESTORE_TIMEOUT, e);
        } catch (ExecutionException e) {
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        }
    }

    private com.google.cloud.firestore.CollectionReference imageCollection(String diaryId) {
        return firestore.collection(DIARIES_COLLECTION)
            .document(diaryId)
            .collection(IMAGES_COLLECTION);
    }

    private String requireDiaryId(Image image) {
        if (image.getDiary() == null || image.getDiary().getDiaryId() == null) {
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR);
        }
        return image.getDiary().getDiaryId();
    }

    private boolean isLiveImage(DocumentSnapshot snapshot) {
        return snapshot.exists() && snapshot.get(ImageDocumentMapper.FIELD_DELETED_AT) == null;
    }

    private String generateId() {
        return IdGenerator.generate();
    }
}
