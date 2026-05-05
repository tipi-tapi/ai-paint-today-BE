package tipitapi.drawmytoday.domain.oauth.repository;

import com.google.cloud.firestore.Firestore;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.domain.oauth.domain.Auth;
import tipitapi.drawmytoday.domain.user.domain.User;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FirestoreAuthRepository implements AuthRepository {

    private static final String COLLECTION = "auth";
    private static final long TIMEOUT_SECONDS = 5L;

    private final Firestore firestore;
    private final AuthDocumentMapper mapper;

    @Override
    public Auth save(Auth auth) {
        try {
            if (auth.getAuthId() != null) {
                firestore.collection(COLLECTION)
                    .document(String.valueOf(auth.getAuthId()))
                    .set(mapper.toDocument(auth))
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                return auth;
            }

            Long authId = generateUniqueAuthId();
            var newAuth = Auth.restore(
                authId,
                auth.getUser(),
                auth.getRefreshToken(),
                LocalDateTime.now()
            );
            firestore.collection(COLLECTION)
                .document(String.valueOf(authId))
                .set(mapper.toDocument(newAuth))
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            return newAuth;
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
    public Optional<Auth> findByUser(User user) {
        try {
            String userId = user != null && user.getUserId() != null
                ? String.valueOf(user.getUserId())
                : null;
            var documents = firestore.collection(COLLECTION)
                .whereEqualTo(AuthDocumentMapper.FIELD_USER_ID, userId)
                .limit(1)
                .get()
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .getDocuments();
            return documents.stream().findFirst().map(mapper::fromDocument);
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
    public Optional<Auth> findByRefreshToken(String refreshToken) {
        try {
            var documents = firestore.collection(COLLECTION)
                .whereEqualTo(AuthDocumentMapper.FIELD_REFRESH_TOKEN, refreshToken)
                .limit(1)
                .get()
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .getDocuments();
            return documents.stream().findFirst().map(mapper::fromDocument);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        } catch (TimeoutException e) {
            throw new BusinessException(ErrorCode.FIRESTORE_TIMEOUT, e);
        } catch (ExecutionException e) {
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR, e);
        }
    }

    private Long generateUniqueAuthId() throws InterruptedException, ExecutionException, TimeoutException {
        int maxAttempts = 5;
        for (int i = 0; i < maxAttempts; i++) {
            Long candidateId = System.currentTimeMillis() * 1000L + ThreadLocalRandom.current().nextInt(1000);
            boolean exists = firestore.collection(COLLECTION)
                .document(String.valueOf(candidateId))
                .get()
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .exists();
            if (!exists) {
                return candidateId;
            }
        }
        throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR);
    }
}
