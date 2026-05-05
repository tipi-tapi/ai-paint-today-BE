package tipitapi.drawmytoday.domain.adreward.repository;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import tipitapi.drawmytoday.domain.adreward.domain.AdReward;
import tipitapi.drawmytoday.domain.user.domain.User;

@Component
public class AdRewardDocumentMapper {

    static final String FIELD_AD_REWARD_ID = "adRewardId";
    static final String FIELD_USER_ID = "userId";
    static final String FIELD_USED_AT = "usedAt";
    static final String FIELD_CREATED_AT = "createdAt";

    public Map<String, Object> toDocument(AdReward adReward) {
        var doc = new HashMap<String, Object>();
        doc.put(FIELD_AD_REWARD_ID, adReward.getAdRewardId());
        doc.put(FIELD_USER_ID, userId(adReward));
        doc.put(FIELD_USED_AT, toTimestamp(adReward.getUsedAt()));
        doc.put(FIELD_CREATED_AT, toTimestamp(adReward.getCreatedAt()));
        return doc;
    }

    public AdReward fromDocument(DocumentSnapshot snapshot) {
        Long adRewardId = toLong(snapshot.get(FIELD_AD_REWARD_ID), snapshot.getId());
        User user = toUser(snapshot);
        LocalDateTime usedAt = toLocalDateTime(snapshot.get(FIELD_USED_AT));
        LocalDateTime createdAt = toLocalDateTime(snapshot.get(FIELD_CREATED_AT));
        return AdReward.restore(adRewardId, user, usedAt, createdAt);
    }

    private Long userId(AdReward adReward) {
        if (adReward.getUser() == null) {
            return null;
        }
        return adReward.getUser().getUserId();
    }

    private User toUser(DocumentSnapshot snapshot) {
        Long parsed = toLong(snapshot.get(FIELD_USER_ID), parentUserId(snapshot));
        if (parsed == null) {
            return null;
        }
        return User.restore(parsed, null, null, null, null, null, null, null);
    }

    private String parentUserId(DocumentSnapshot snapshot) {
        var parent = snapshot.getReference().getParent().getParent();
        return parent != null ? parent.getId() : null;
    }

    private Long toLong(Object value, String fallback) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            return Long.parseLong((String) value);
        }
        if (fallback == null) {
            return null;
        }
        return Long.parseLong(fallback);
    }

    private Timestamp toTimestamp(LocalDateTime ldt) {
        if (ldt == null) {
            return null;
        }
        var instant = ldt.atZone(ZoneId.systemDefault()).toInstant();
        return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Timestamp) {
            return ((Timestamp) value).toDate().toInstant()
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
}
