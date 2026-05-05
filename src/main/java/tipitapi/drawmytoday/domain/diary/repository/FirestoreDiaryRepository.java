package tipitapi.drawmytoday.domain.diary.repository;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.domain.admin.dto.GetDiaryAdminResponse;
import tipitapi.drawmytoday.domain.admin.dto.GetDiaryNoteAndPromptResponse;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.dto.GetMonthlyDiariesResponse;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FirestoreDiaryRepository implements DiaryRepository {

    static final String DIARIES_COLLECTION = "diaries";
    static final String IMAGES_COLLECTION = "images";
    private static final long TIMEOUT_SECONDS = 5L;

    private final Firestore firestore;
    private final DiaryDocumentMapper diaryMapper;

    @Override
    public Diary save(Diary diary) {
        try {
            Diary target = diary.getDiaryId() != null ? restoreForSave(diary) : restoreNewDiary(diary);
            var ref = firestore.collection(DIARIES_COLLECTION).document(String.valueOf(target.getDiaryId()));
            var existing = ref.get().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            Object selectedImage = existing.exists()
                ? existing.get(DiaryDocumentMapper.FIELD_SELECTED_IMAGE)
                : null;
            Long imageCount = existing.exists()
                ? existing.getLong(DiaryDocumentMapper.FIELD_IMAGE_COUNT)
                : null;
            ref.set(diaryMapper.toDocument(target, selectedImage, imageCount != null ? imageCount : 0L))
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
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
    public List<Diary> saveAll(List<Diary> diaries) {
        return diaries.stream().map(this::save).collect(Collectors.toList());
    }

    @Override
    public Optional<Diary> findById(Long diaryId) {
        try {
            var snapshot = firestore.collection(DIARIES_COLLECTION)
                .document(String.valueOf(diaryId))
                .get()
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!isLiveDiary(snapshot)) {
                return Optional.empty();
            }
            return Optional.of(diaryMapper.fromDocument(snapshot));
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
    public void delete(Diary diary) {
        try {
            firestore.collection(DIARIES_COLLECTION)
                .document(String.valueOf(diary.getDiaryId()))
                .update(DiaryDocumentMapper.FIELD_DELETED_AT,
                    diaryMapper.toDocument(Diary.restore(diary.getDiaryId(), diary.getUser(),
                        diary.getEmotion(), diary.getDiaryDate(), diary.getNotes(), diary.isAi(),
                        diary.getTitle(), diary.getWeather(), diary.getImageList(),
                        LocalDateTime.now(), diary.isTest(), diary.getCreatedAt(),
                        diary.getUpdatedAt()), null, 0).get(DiaryDocumentMapper.FIELD_DELETED_AT))
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
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
    public void flush() {
    }

    @Override
    public List<Diary> findAllByUserUserIdAndDiaryDateBetween(Long userId, LocalDateTime startMonth,
        LocalDateTime endMonth) {
        return findLiveDiaries().stream()
            .filter(diary -> diary.getUser() != null && userId.equals(diary.getUser().getUserId()))
            .filter(diary -> isBetween(diary.getDiaryDate(), startMonth, endMonth))
            .sorted(Comparator.comparing(Diary::getDiaryDate, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Diary> findFirstByUserUserIdOrderByCreatedAtDesc(Long userId) {
        return findLiveDiaries().stream()
            .filter(diary -> diary.getUser() != null && userId.equals(diary.getUser().getUserId()))
            .max(Comparator.comparing(Diary::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    @Override
    public Page<GetDiaryAdminResponse> getDiariesForMonitorAsPage(Pageable pageable,
        Direction direction, Long emotionId, boolean withTest) {
        List<GetDiaryAdminResponse> all = findLiveDiarySnapshots().stream()
            .filter(snapshot -> withTest || !Boolean.TRUE.equals(snapshot.getBoolean(DiaryDocumentMapper.FIELD_IS_TEST)))
            .filter(snapshot -> emotionId == null || emotionId.equals(emotionId(snapshot)))
            .flatMap(snapshot -> adminResponses(snapshot).stream())
            .sorted((left, right) -> compareByImageCreatedAt(left, right, direction))
            .collect(Collectors.toList());
        int start = (int) Math.min(pageable.getOffset(), all.size());
        int end = Math.min(start + pageable.getPageSize(), all.size());
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
            Sort.by(direction, "imageCreatedAt"));
        return new PageImpl<>(all.subList(start, end), sortedPageable, all.size());
    }

    @Override
    public Optional<Diary> getDiaryExistsByDiaryDate(Long userId, LocalDate diaryDate) {
        return findAllByUserUserIdAndDiaryDateBetween(userId, diaryDate.atStartOfDay(),
            diaryDate.atTime(23, 59, 59)).stream().findFirst();
    }

    @Override
    public List<GetMonthlyDiariesResponse> getMonthlyDiaries(Long userId, LocalDateTime startMonth,
        LocalDateTime endMonth) {
        return findLiveDiarySnapshots().stream()
            .filter(snapshot -> userId.equals(snapshot.getLong(DiaryDocumentMapper.FIELD_USER_ID)))
            .filter(snapshot -> isBetween(diaryMapper.fromDocument(snapshot).getDiaryDate(), startMonth, endMonth))
            .sorted(Comparator.comparing(snapshot -> diaryMapper.fromDocument(snapshot).getDiaryDate(),
                Comparator.nullsLast(Comparator.naturalOrder())))
            .map(snapshot -> GetMonthlyDiariesResponse.of(
                Long.parseLong(snapshot.getId()),
                selectedImageUrl(snapshot),
                diaryMapper.fromDocument(snapshot).getDiaryDate()))
            .collect(Collectors.toList());
    }

    @Override
    public List<GetDiaryNoteAndPromptResponse> getDiaryNoteAndPrompt() {
        try {
            return firestore.collectionGroup(IMAGES_COLLECTION)
                .get()
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .getDocuments()
                .stream()
                .filter(this::isLiveImage)
                .filter(snapshot -> snapshot.get("prompt.promptGeneratorContent") == null)
                .filter(snapshot -> {
                    String prompt = snapshot.getString("prompt.promptText");
                    return prompt != null && !prompt.contains(", portrait");
                })
                .limit(10L)
                .map(snapshot -> {
                    DocumentSnapshot diary = parentDiary(snapshot);
                    return new GetDiaryNoteAndPromptResponse(
                        toLong(snapshot.get("prompt.promptId")),
                        diary != null ? diary.getString(DiaryDocumentMapper.FIELD_NOTES) : null,
                        snapshot.getString("prompt.promptText")
                    );
                })
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

    private List<Diary> findLiveDiaries() {
        return findLiveDiarySnapshots().stream().map(diaryMapper::fromDocument).collect(Collectors.toList());
    }

    private List<DocumentSnapshot> findLiveDiarySnapshots() {
        try {
            return firestore.collection(DIARIES_COLLECTION)
                .get()
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .getDocuments()
                .stream()
                .filter(this::isLiveDiary)
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

    private List<GetDiaryAdminResponse> adminResponses(DocumentSnapshot diarySnapshot) {
        try {
            return diarySnapshot.getReference().collection(IMAGES_COLLECTION)
                .get()
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .getDocuments()
                .stream()
                .filter(this::isLiveImage)
                .map(image -> new GetDiaryAdminResponse(
                    Long.parseLong(diarySnapshot.getId()),
                    image.getString(ImageDocumentMapper.FIELD_IMAGE_URL),
                    image.getString("prompt.promptText"),
                    diaryMapper.fromDocument(diarySnapshot).getCreatedAt(),
                    toLocalDateTime(image.get(ImageDocumentMapper.FIELD_CREATED_AT)),
                    image.getString(ImageDocumentMapper.FIELD_REVIEW),
                    Boolean.TRUE.equals(diarySnapshot.getBoolean(DiaryDocumentMapper.FIELD_IS_TEST))
                ))
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

    private DocumentSnapshot parentDiary(DocumentSnapshot imageSnapshot) {
        try {
            var parent = imageSnapshot.getReference().getParent().getParent();
            if (parent == null) {
                return null;
            }
            var diary = parent.get().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            return isLiveDiary(diary) ? diary : null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        } catch (TimeoutException e) {
            throw new BusinessException(ErrorCode.FIRESTORE_TIMEOUT, e);
        } catch (ExecutionException e) {
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        }
    }

    private Diary restoreNewDiary(Diary diary) {
        LocalDateTime now = LocalDateTime.now();
        return Diary.restore(generateId(), diary.getUser(), diary.getEmotion(), diary.getDiaryDate(),
            diary.getNotes(), diary.isAi(), diary.getTitle(), diary.getWeather(), diary.getImageList(),
            diary.getDeletedAt(), diary.isTest(), now, now);
    }

    private Diary restoreForSave(Diary diary) {
        LocalDateTime now = LocalDateTime.now();
        return Diary.restore(diary.getDiaryId(), diary.getUser(), diary.getEmotion(), diary.getDiaryDate(),
            diary.getNotes(), diary.isAi(), diary.getTitle(), diary.getWeather(), diary.getImageList(),
            diary.getDeletedAt(), diary.isTest(), diary.getCreatedAt() != null ? diary.getCreatedAt() : now, now);
    }

    private boolean isLiveDiary(DocumentSnapshot snapshot) {
        return snapshot.exists() && snapshot.get(DiaryDocumentMapper.FIELD_DELETED_AT) == null;
    }

    private boolean isLiveImage(DocumentSnapshot snapshot) {
        return snapshot.exists() && snapshot.get(ImageDocumentMapper.FIELD_DELETED_AT) == null;
    }

    private boolean isBetween(LocalDateTime value, LocalDateTime start, LocalDateTime end) {
        return value != null && !value.isBefore(start) && !value.isAfter(end);
    }

    private String selectedImageUrl(DocumentSnapshot snapshot) {
        Object selectedImage = snapshot.get(DiaryDocumentMapper.FIELD_SELECTED_IMAGE);
        if (selectedImage instanceof java.util.Map) {
            Object imageUrl = ((java.util.Map<?, ?>) selectedImage).get(ImageDocumentMapper.FIELD_IMAGE_URL);
            return imageUrl != null ? String.valueOf(imageUrl) : null;
        }
        return null;
    }

    private Long emotionId(DocumentSnapshot snapshot) {
        Object emotion = snapshot.get(DiaryDocumentMapper.FIELD_EMOTION);
        if (emotion instanceof java.util.Map) {
            return toLong(((java.util.Map<?, ?>) emotion).get("emotionId"));
        }
        return null;
    }

    private int compareByImageCreatedAt(GetDiaryAdminResponse left, GetDiaryAdminResponse right, Direction direction) {
        Comparator<LocalDateTime> comparator = Comparator.nullsLast(Comparator.naturalOrder());
        int result = comparator.compare(left.getImageCreatedAt(), right.getImageCreatedAt());
        return direction.isAscending() ? result : -result;
    }

    private Long toLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            return Long.parseLong((String) value);
        }
        return null;
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof com.google.cloud.Timestamp) {
            return ((com.google.cloud.Timestamp) value).toDate().toInstant()
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

    private Long generateId() {
        return System.currentTimeMillis() * 1000L + ThreadLocalRandom.current().nextInt(1000);
    }
}
