package tipitapi.drawmytoday.migration;

import com.fasterxml.jackson.databind.ObjectMapper;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tipitapi.drawmytoday.migration.dto.SampleData;
import tipitapi.drawmytoday.migration.dto.SampleData.SampleDiary;
import tipitapi.drawmytoday.migration.dto.SampleData.SampleEmotion;
import tipitapi.drawmytoday.migration.dto.SampleData.SampleImage;
import tipitapi.drawmytoday.migration.dto.SampleData.SamplePrompt;
import tipitapi.drawmytoday.migration.dto.SampleData.SampleUser;
import tipitapi.drawmytoday.migration.mapper.FirestoreDocumentMapper;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Profile("bulk-load")
@RequiredArgsConstructor
@Slf4j
public class BulkLoadJob implements CommandLineRunner {

    private static final int BATCH_SIZE = 400;
    private static final int LOG_INTERVAL = 1000;

    private final Firestore firestore;
    private final FirestoreDocumentMapper mapper;
    private final BulkLoadVerifier verifier;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void verifyEmulator() {
        var emulatorHost = System.getenv("FIRESTORE_EMULATOR_HOST");
        var forceProduction = "true".equals(System.getenv("BULK_LOAD_FORCE_PRODUCTION"));

        if (emulatorHost == null && !forceProduction) {
            throw new IllegalStateException(
                "FIRESTORE_EMULATOR_HOST not set. " +
                "Bulk load to production Firestore requires BULK_LOAD_FORCE_PRODUCTION=true. " +
                "This is a safety check to prevent accidental production writes.");
        }

        if (emulatorHost != null) {
            log.info("Bulk load target: Firestore Emulator at {}", emulatorHost);
        } else {
            log.warn("Bulk load target: PRODUCTION Firestore. BULK_LOAD_FORCE_PRODUCTION is set.");
        }
    }

    @Override
    public void run(String... args) throws Exception {
        var globalStart = Instant.now();
        var deltaMode = "true".equals(System.getenv("BULK_LOAD_DELTA_MODE"));
        if (deltaMode) {
            log.warn("DELTA MODE enabled: input is treated as a partial diff. " +
                "Only documents present in the file are written; collection-count " +
                "verification is skipped.");
        }
        var data = loadSampleData();
        applyEmailFilter(data);

        // filteredDiaries must be built before imagesByDiary so we can exclude
        // images that belong to is_test diaries — avoiding false negatives in the verifier.
        var filteredDiaries = data.getDiaries().stream()
            .filter(d -> !Boolean.TRUE.equals(d.getIsTest()))
            .collect(Collectors.toList());
        var filteredDiaryIds = filteredDiaries.stream()
            .map(SampleDiary::getDiaryId)
            .collect(Collectors.toSet());

        var emotionMap = data.getEmotions().stream()
            .collect(Collectors.toMap(SampleEmotion::getEmotionId, e -> e));
        var promptMap = data.getPrompts().stream()
            .collect(Collectors.toMap(SamplePrompt::getPromptId, p -> p));
        var imagesByDiary = data.getImages().stream()
            .filter(img -> filteredDiaryIds.contains(img.getDiaryId()))
            .collect(Collectors.groupingBy(SampleImage::getDiaryId));

        loadEmotions(data.getEmotions());
        loadUsers(data.getUsers());
        loadDiaries(filteredDiaries, emotionMap, imagesByDiary, promptMap);
        loadImages(filteredDiaries, imagesByDiary, promptMap);
        // tickets / adRewards are intentionally NOT migrated to Firestore.

        var elapsed = Duration.between(globalStart, Instant.now()).getSeconds();
        log.info("=== Bulk load complete in {}s. Loaded: {} emotions, {} users, " +
                 "{} diaries, {} images ===",
            elapsed,
            data.getEmotions().size(), data.getUsers().size(),
            filteredDiaries.size(),
            imagesByDiary.values().stream().mapToLong(List::size).sum());

        verifier.verify(data, imagesByDiary, deltaMode);
    }

    private SampleData loadSampleData() throws Exception {
        var path = Optional.ofNullable(System.getenv("SAMPLE_DATA_PATH"))
            .orElse("./sample-data.json");
        var file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException(
                "sample-data.json not found at: " + file.getAbsolutePath() +
                ". Set SAMPLE_DATA_PATH to the correct location.");
        }
        log.info("Loading sample data from: {}", file.getAbsolutePath());
        return objectMapper.readValue(file, SampleData.class);
    }

    /**
     * 테스트 용도: BULK_LOAD_FILTER_EMAIL 이 설정되면 해당 email 사용자와
     * 그 사용자가 작성한 diary 만 적재 대상으로 남긴다.
     * images 는 run() 에서 diary id 기준으로 자동으로 좁혀지고, prompt 는
     * image 에 비정규화되어 lookup 으로만 쓰이므로 별도 필터가 필요 없다.
     * emotions 는 마스터 데이터라 전체 유지한다.
     */
    private void applyEmailFilter(SampleData data) {
        var email = System.getenv("BULK_LOAD_FILTER_EMAIL");
        if (email == null || email.isBlank()) {
            return;
        }

        log.warn("EMAIL FILTER enabled: migrating only user='{}' and their diaries/prompts", email);

        var targetUserIds = data.getUsers().stream()
            .filter(u -> email.equals(u.getEmail()))
            .filter(u -> u.getDeletedAt() == null)
            .map(SampleUser::getUserId)
            .collect(Collectors.toSet());

        if (targetUserIds.isEmpty()) {
            throw new IllegalArgumentException(
                "BULK_LOAD_FILTER_EMAIL set but no user found with email: " + email);
        }

        data.getUsers().removeIf(u -> !targetUserIds.contains(u.getUserId()));
        data.getDiaries().removeIf(d -> !targetUserIds.contains(d.getUserId()));

        log.warn("EMAIL FILTER result: {} user(s), {} diaries retained",
            data.getUsers().size(), data.getDiaries().size());
    }

    private void loadEmotions(List<SampleEmotion> items) throws Exception {
        var start = startCollection("emotions", items.size());
        var state = new BatchState("emotions");
        for (var item : items) {
            var ref = firestore.collection("emotions").document(String.valueOf(item.getEmotionId()));
            state.batch.set(ref, mapper.toEmotionDoc(item));
            state.advance(items.size());
            if (state.shouldCommit()) commitAndReset(state, "emotions");
        }
        flushAndLog(state, "emotions", items.size(), start);
    }

    private void loadUsers(List<SampleUser> items) throws Exception {
        var start = startCollection("users", items.size());
        var state = new BatchState("users");
        for (var item : items) {
            var ref = firestore.collection("users").document(String.valueOf(item.getUserId()));
            state.batch.set(ref, mapper.toUserDoc(item));
            state.advance(items.size());
            if (state.shouldCommit()) commitAndReset(state, "users");
        }
        flushAndLog(state, "users", items.size(), start);
    }

    private void loadDiaries(
            List<SampleDiary> diaries,
            Map<Long, SampleEmotion> emotionMap,
            Map<Long, List<SampleImage>> imagesByDiary,
            Map<Long, SamplePrompt> promptMap) throws Exception {
        var start = startCollection("diaries", diaries.size());
        var state = new BatchState("diaries");
        int skipped = 0;

        for (var diary : diaries) {
            var emotion = emotionMap.get(diary.getEmotionId());
            if (emotion == null) {
                log.warn("emotion not found for diary_id={} emotion_id={} — skipping",
                    diary.getDiaryId(), diary.getEmotionId());
                skipped++;
                continue;
            }
            var images = imagesByDiary.getOrDefault(diary.getDiaryId(), Collections.emptyList());
            var ref = firestore.collection("diaries").document(String.valueOf(diary.getDiaryId()));
            state.batch.set(ref, mapper.toDiaryDoc(diary, emotion, images, promptMap));
            state.advance(diaries.size());
            if (state.shouldCommit()) commitAndReset(state, "diaries");
        }

        flushAndLog(state, "diaries", diaries.size() - skipped, start);
        if (skipped > 0) log.warn("diaries skipped (missing emotion): {}", skipped);
    }

    private void loadImages(
            List<SampleDiary> diaries,
            Map<Long, List<SampleImage>> imagesByDiary,
            Map<Long, SamplePrompt> promptMap) throws Exception {
        int total = (int) imagesByDiary.values().stream().mapToLong(List::size).sum();
        var start = startCollection("images (subcollection)", total);
        var state = new BatchState("diaries/images");

        for (var diary : diaries) {
            var images = imagesByDiary.getOrDefault(diary.getDiaryId(), Collections.emptyList());
            for (var image : images) {
                SamplePrompt prompt = null;
                if (image.getPromptId() != null) {
                    prompt = promptMap.get(image.getPromptId());
                    if (prompt == null) {
                        log.warn("prompt not found for image_id={} prompt_id={} — storing prompt=null",
                            image.getImageId(), image.getPromptId());
                    }
                }
                var ref = firestore.collection("diaries")
                    .document(String.valueOf(diary.getDiaryId()))
                    .collection("images")
                    .document(String.valueOf(image.getImageId()));
                state.batch.set(ref, mapper.toImageDoc(image, prompt));
                state.advance(total);
                if (state.shouldCommit()) commitAndReset(state, "diaries/images");
            }
        }
        flushAndLog(state, "images", total, start);
    }

    private Instant startCollection(String name, int total) {
        log.info("[INFO] Loading {}... ({} items)", name, total);
        return Instant.now();
    }

    private void flushAndLog(BatchState state, String name, int count, Instant start) throws Exception {
        if (state.hasPending()) {
            commitAndReset(state, name);
        }
        var elapsed = Duration.between(start, Instant.now()).toMillis() / 1000.0;
        log.info("[INFO] {} loaded: {} items in {}s", name, count, elapsed);
    }

    private void commitAndReset(BatchState state, String collection) throws Exception {
        try {
            state.batch.commit().get();
            log.debug("Committed batch #{} for {}", state.batchNum, collection);
        } catch (Exception e) {
            log.error("WriteBatch commit failed for collection={} batch#{}: {}",
                collection, state.batchNum, e.getMessage());
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        }
        state.batch = firestore.batch();
        state.batchNum++;
        state.pendingWrites = 0;
    }

    /** Mutable batch tracking state. pendingWrites tracks actual uncommitted writes
     *  independently of count (which can skip items), preventing flush misses. */
    private class BatchState {
        WriteBatch batch = firestore.batch();
        int count = 0;
        int pendingWrites = 0;
        int batchNum = 1;
        final String name;

        BatchState(String name) { this.name = name; }

        void advance(int total) {
            count++;
            pendingWrites++;
            if (count % LOG_INTERVAL == 0) {
                int pct = total > 0 ? (count * 100 / total) : 100;
                log.info("[INFO] {}: {}/{} loaded ({}%)", name, count, total, pct);
            }
        }

        boolean shouldCommit() { return pendingWrites >= BATCH_SIZE; }
        boolean hasPending() { return pendingWrites > 0; }
    }
}
