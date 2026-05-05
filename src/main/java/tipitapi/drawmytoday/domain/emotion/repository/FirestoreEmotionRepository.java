package tipitapi.drawmytoday.domain.emotion.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FirestoreEmotionRepository implements EmotionRepository {

    private static final String COLLECTION = "emotions";
    private static final String FIELD_IS_ACTIVE = "isActive";
    private static final int BATCH_LIMIT = 400;

    private final Firestore firestore;
    private final EmotionDocumentMapper mapper;

    @Override
    public List<Emotion> findAllActiveEmotions() {
        try {
            return firestore.collection(COLLECTION)
                .whereEqualTo(FIELD_IS_ACTIVE, true)
                .get().get().getDocuments()
                .stream()
                .map(mapper::fromDocument)
                .sorted(Comparator.comparing(Emotion::getEmotionId))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        }
    }

    @Override
    public Optional<Emotion> findById(Long emotionId) {
        try {
            var snapshot = firestore.collection(COLLECTION)
                .document(String.valueOf(emotionId))
                .get().get();
            return snapshot.exists() ? Optional.of(mapper.fromDocument(snapshot)) : Optional.empty();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        }
    }

    @Override
    public Emotion save(Emotion emotion) {
        try {
            String docId = emotion.getEmotionId() != null
                ? String.valueOf(emotion.getEmotionId())
                : UUID.randomUUID().toString();
            firestore.collection(COLLECTION).document(docId)
                .set(mapper.toDocument(emotion)).get();
            if (emotion.getEmotionId() != null) {
                return findById(emotion.getEmotionId()).orElse(emotion);
            }
            return emotion;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        }
    }

    @Override
    public List<Emotion> saveAll(List<Emotion> emotions) {
        try {
            for (var batch : partition(emotions, BATCH_LIMIT)) {
                WriteBatch writeBatch = firestore.batch();
                for (var emotion : batch) {
                    String docId = emotion.getEmotionId() != null
                        ? String.valueOf(emotion.getEmotionId())
                        : UUID.randomUUID().toString();
                    writeBatch.set(
                        firestore.collection(COLLECTION).document(docId),
                        mapper.toDocument(emotion));
                }
                writeBatch.commit().get();
            }
            return emotions;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        }
    }

    private static <T> List<List<T>> partition(List<T> list, int size) {
        var result = new ArrayList<List<T>>();
        for (int i = 0; i < list.size(); i += size) {
            result.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return result;
    }
}
