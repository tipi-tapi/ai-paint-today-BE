package tipitapi.drawmytoday.domain.user.repository;

import com.google.cloud.firestore.Firestore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.domain.UserRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FirestoreUserRepository implements UserRepository {

    private static final String COLLECTION = "users";
    private static final String FIELD_DELETED_AT = "deletedAt";

    private final Firestore firestore;
    private final UserDocumentMapper mapper;

    @Override
    public User save(User user) {
        try {
            if (user.getUserId() != null) {
                var ref = firestore.collection(COLLECTION).document(String.valueOf(user.getUserId()));
                ref.set(mapper.toDocument(user)).get();
                return user;
            }
            // 신규 생성: 충돌 없는 ID 생성 + createdAt/updatedAt 스탬프
            Long userId = generateUniqueUserId();
            var now = LocalDateTime.now();
            var newUser = User.restore(
                userId,
                user.getEmail(),
                user.getSocialCode(),
                user.getUserRole() != null ? user.getUserRole() : UserRole.USER,
                user.getLastDiaryDate(),
                user.getDeletedAt(),
                now,
                now
            );
            firestore.collection(COLLECTION).document(String.valueOf(userId))
                .set(mapper.toDocument(newUser)).get();
            return newUser;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        } catch (ExecutionException e) {
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        }
    }

    @Override
    public Optional<User> findByUserId(Long userId) {
        try {
            var snapshot = firestore.collection(COLLECTION)
                .document(String.valueOf(userId))
                .get().get();
            if (!snapshot.exists() || snapshot.getTimestamp(FIELD_DELETED_AT) != null) {
                return Optional.empty();
            }
            return Optional.of(mapper.fromDocument(snapshot));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        } catch (ExecutionException e) {
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        }
    }

    @Override
    public List<User> findAllByEmail(String email) {
        try {
            // whereEqualTo(field, null)은 Firestore SDK에 따라 동작이 불안정하므로
            // in-stream 필터링으로 findByUserId와 일관성 보장
            return firestore.collection(COLLECTION)
                .whereEqualTo(UserDocumentMapper.FIELD_EMAIL, email)
                .get().get()
                .getDocuments()
                .stream()
                .filter(doc -> doc.getTimestamp(FIELD_DELETED_AT) == null)
                .map(mapper::fromDocument)
                .collect(Collectors.toList());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        } catch (ExecutionException e) {
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        }
    }

    private Long generateUniqueUserId() throws InterruptedException, ExecutionException {
        int maxAttempts = 5;
        for (int i = 0; i < maxAttempts; i++) {
            Long candidateId = System.currentTimeMillis() * 1000L + ThreadLocalRandom.current().nextInt(1000);
            if (!firestore.collection(COLLECTION).document(String.valueOf(candidateId)).get().get().exists()) {
                return candidateId;
            }
        }
        throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR);
    }
}
