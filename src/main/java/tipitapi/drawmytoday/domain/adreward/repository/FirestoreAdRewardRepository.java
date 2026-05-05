package tipitapi.drawmytoday.domain.adreward.repository;

import com.google.cloud.firestore.Firestore;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.domain.adreward.domain.AdReward;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FirestoreAdRewardRepository implements AdRewardRepository {

    private static final String USERS_COLLECTION = "users";
    private static final String AD_REWARDS_COLLECTION = "adRewards";
    private static final long TIMEOUT_SECONDS = 5L;

    private final Firestore firestore;
    private final AdRewardDocumentMapper mapper;

    @Override
    public AdReward save(AdReward adReward) {
        try {
            Long userId = requireUserId(adReward);
            AdReward target = adReward.getAdRewardId() != null ? adReward : restoreNewAdReward(adReward);
            adRewardCollection(userId)
                .document(String.valueOf(target.getAdRewardId()))
                .set(mapper.toDocument(target))
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
    public List<AdReward> findAllByUserId(Long userId) {
        try {
            return adRewardCollection(userId)
                .get()
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .getDocuments()
                .stream()
                .map(mapper::fromDocument)
                .sorted(Comparator.comparing(AdReward::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
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
    public List<AdReward> findAllByUserIdAndUsedAtIsNull(Long userId) {
        try {
            return adRewardCollection(userId)
                .get()
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .getDocuments()
                .stream()
                .filter(doc -> doc.getTimestamp(AdRewardDocumentMapper.FIELD_USED_AT) == null)
                .map(mapper::fromDocument)
                .sorted(Comparator.comparing(AdReward::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
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
    public List<AdReward> findValidAdReward(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return findAllByUserIdAndUsedAtIsNull(userId).stream()
            .filter(adReward -> isCreatedBetween(adReward, startDate, endDate))
            .collect(Collectors.toList());
    }

    private boolean isCreatedBetween(AdReward adReward, LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime createdAt = adReward.getCreatedAt();
        if (createdAt == null) {
            return false;
        }
        return !createdAt.isBefore(startDate) && !createdAt.isAfter(endDate);
    }

    private com.google.cloud.firestore.CollectionReference adRewardCollection(Long userId) {
        return firestore.collection(USERS_COLLECTION)
            .document(String.valueOf(userId))
            .collection(AD_REWARDS_COLLECTION);
    }

    private Long requireUserId(AdReward adReward) {
        if (adReward.getUser() == null || adReward.getUser().getUserId() == null) {
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR);
        }
        return adReward.getUser().getUserId();
    }

    private AdReward restoreNewAdReward(AdReward adReward) {
        return AdReward.restore(
            generateAdRewardId(),
            adReward.getUser(),
            adReward.getUsedAt(),
            adReward.getCreatedAt() != null ? adReward.getCreatedAt() : LocalDateTime.now()
        );
    }

    private Long generateAdRewardId() {
        return System.currentTimeMillis() * 1000L + ThreadLocalRandom.current().nextInt(1000);
    }
}
