package tipitapi.drawmytoday.migration;

import com.google.cloud.firestore.Firestore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tipitapi.drawmytoday.migration.dto.SampleData;
import tipitapi.drawmytoday.migration.dto.SampleData.SampleDiary;
import tipitapi.drawmytoday.migration.dto.SampleData.SampleImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Profile("bulk-load")
@RequiredArgsConstructor
@Slf4j
public class BulkLoadVerifier {

    private static final int SAMPLE_SIZE = 5;

    private final Firestore firestore;

    public void verify(SampleData data, Map<Long, List<SampleImage>> imagesByDiary, boolean deltaMode) throws Exception {
        log.info("=== Starting verification ===");
        boolean passed = true;

        if (deltaMode) {
            // In delta mode the input holds only changed documents, so the total
            // Firestore count will not match the file. Skip count checks and rely
            // on the per-document sample checks below.
            log.info("[DELTA] skipping collection count verification");
        } else {
            passed &= verifyCollectionCounts(data, imagesByDiary);
        }
        passed &= verifyDiarySample(data.getDiaries(), imagesByDiary);
        passed &= verifyImageSample(imagesByDiary);

        if (!passed) {
            log.error("=== Verification FAILED. See errors above. ===");
            System.exit(1);
        }
        log.info("=== All verifications PASSED ===");
    }

    private boolean verifyCollectionCounts(SampleData data, Map<Long, List<SampleImage>> imagesByDiary) throws Exception {
        boolean ok = true;

        ok &= verifyTopLevelCount("emotions", data.getEmotions().size());
        ok &= verifyTopLevelCount("users", data.getUsers().size());

        long expectedDiaries = data.getDiaries().stream()
            .filter(d -> !Boolean.TRUE.equals(d.getIsTest()))
            .count();
        ok &= verifyTopLevelCount("diaries", (int) expectedDiaries);

        return ok;
    }

    private boolean verifyTopLevelCount(String collection, int expected) throws Exception {
        long actual = firestore.collection(collection).count().get().get().getCount();
        if (actual != expected) {
            log.error("[VERIFY FAIL] {}: expected={} actual={}", collection, expected, actual);
            return false;
        }
        log.info("[VERIFY OK] {}: count={}", collection, actual);
        return true;
    }

    private boolean verifyDiarySample(List<SampleDiary> diaries, Map<Long, List<SampleImage>> imagesByDiary) throws Exception {
        var nonTestDiaries = diaries.stream()
            .filter(d -> !Boolean.TRUE.equals(d.getIsTest()))
            .collect(Collectors.toList());
        var sample = pickRandom(nonTestDiaries, SAMPLE_SIZE);
        boolean ok = true;

        for (var diary : sample) {
            var diaryId = String.valueOf(diary.getDiaryId());
            var doc = firestore.collection("diaries").document(diaryId).get().get();

            if (!doc.exists()) {
                log.error("[VERIFY FAIL] diary doc not found: {}", diaryId);
                ok = false;
                continue;
            }

            // Verify emotion field
            var emotionField = doc.get("emotion");
            if (emotionField == null) {
                log.error("[VERIFY FAIL] diary {} missing emotion field", diaryId);
                ok = false;
            } else {
                @SuppressWarnings("unchecked")
                var embeddedEmotion = (Map<String, Object>) emotionField;
                var emotionId = String.valueOf(embeddedEmotion.get("emotionId"));
                var emotionDoc = firestore.collection("emotions").document(emotionId).get().get();
                if (!emotionDoc.exists()) {
                    log.error("[VERIFY FAIL] diary {} emotion.emotionId={} not in emotions collection", diaryId, emotionId);
                    ok = false;
                } else {
                    log.info("[VERIFY OK] diary {} emotion check passed (emotionId={})", diaryId, emotionId);
                }
            }

            // Verify selectedImage field
            var selectedImageField = doc.get("selectedImage");
            if (selectedImageField != null) {
                @SuppressWarnings("unchecked")
                var selectedImage = (Map<String, Object>) selectedImageField;
                var imageId = String.valueOf(selectedImage.get("imageId"));
                var imageDoc = firestore.collection("diaries").document(diaryId)
                    .collection("images").document(imageId).get().get();

                if (!imageDoc.exists()) {
                    log.error("[VERIFY FAIL] diary {} selectedImage.imageId={} not found in subcollection", diaryId, imageId);
                    ok = false;
                } else {
                    var subImageUrl = imageDoc.getString("imageUrl");
                    var embeddedImageUrl = (String) selectedImage.get("imageUrl");
                    if (!Objects.equals(subImageUrl, embeddedImageUrl)) {
                        log.error("[VERIFY FAIL] diary {} selectedImage.imageUrl mismatch: embedded={} subcollection={}",
                            diaryId, embeddedImageUrl, subImageUrl);
                        ok = false;
                    } else {
                        log.info("[VERIFY OK] diary {} selectedImage check passed (imageId={})", diaryId, imageId);
                    }
                }
            }
        }
        return ok;
    }

    private boolean verifyImageSample(Map<Long, List<SampleImage>> imagesByDiary) throws Exception {
        var imageDiaryPairs = new ArrayList<long[]>();
        for (var entry : imagesByDiary.entrySet()) {
            for (var img : entry.getValue()) {
                imageDiaryPairs.add(new long[]{img.getImageId(), entry.getKey()});
            }
        }
        Collections.shuffle(imageDiaryPairs);
        var sample = imageDiaryPairs.subList(0, Math.min(SAMPLE_SIZE, imageDiaryPairs.size()));
        boolean ok = true;

        for (var pair : sample) {
            var imageId = String.valueOf(pair[0]);
            var diaryId = String.valueOf(pair[1]);
            var doc = firestore.collection("diaries").document(diaryId)
                .collection("images").document(imageId).get().get();

            if (!doc.exists()) {
                log.error("[VERIFY FAIL] image doc not found: diary={} image={}", diaryId, imageId);
                ok = false;
                continue;
            }

            var promptField = doc.get("prompt");
            if (promptField != null) {
                @SuppressWarnings("unchecked")
                var prompt = (Map<String, Object>) promptField;
                var promptIdRaw = prompt.get("promptId");
                var promptId = promptIdRaw != null ? String.valueOf(promptIdRaw) : null;
                if (promptId == null || promptId.isBlank()) {
                    log.error("[VERIFY FAIL] image {} has non-null prompt but promptId is blank", imageId);
                    ok = false;
                } else {
                    log.info("[VERIFY OK] image {} prompt.promptId={} valid", imageId, promptId);
                }
            }
        }
        return ok;
    }

    private <T> List<T> pickRandom(List<T> list, int n) {
        var copy = new ArrayList<>(list);
        Collections.shuffle(copy);
        return copy.subList(0, Math.min(n, copy.size()));
    }
}
