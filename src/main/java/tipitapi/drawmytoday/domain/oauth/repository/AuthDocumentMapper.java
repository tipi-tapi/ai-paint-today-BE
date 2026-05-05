package tipitapi.drawmytoday.domain.oauth.repository;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import tipitapi.drawmytoday.domain.oauth.domain.Auth;
import tipitapi.drawmytoday.domain.user.domain.User;

@Component
public class AuthDocumentMapper {

    private static final String FIELD_AUTH_ID = "authId";
    static final String FIELD_USER_ID = "userId";
    static final String FIELD_REFRESH_TOKEN = "refreshToken";
    private static final String FIELD_CREATED_AT = "createdAt";

    public Map<String, Object> toDocument(Auth auth) {
        var doc = new HashMap<String, Object>();
        doc.put(FIELD_AUTH_ID, auth.getAuthId() != null ? String.valueOf(auth.getAuthId()) : null);
        doc.put(FIELD_USER_ID, auth.getUser() != null && auth.getUser().getUserId() != null
            ? String.valueOf(auth.getUser().getUserId())
            : null);
        doc.put(FIELD_REFRESH_TOKEN, auth.getRefreshToken());
        doc.put(FIELD_CREATED_AT, toTimestamp(auth.getCreatedAt()));
        return doc;
    }

    public Auth fromDocument(DocumentSnapshot snapshot) {
        Long authId = Long.parseLong(snapshot.getId());
        User user = toUser(snapshot.getString(FIELD_USER_ID));
        String refreshToken = snapshot.getString(FIELD_REFRESH_TOKEN);
        LocalDateTime createdAt = toLocalDateTime(snapshot.get(FIELD_CREATED_AT));
        return Auth.restore(authId, user, refreshToken, createdAt);
    }

    private User toUser(String userId) {
        if (userId == null) {
            return null;
        }
        return User.restore(Long.parseLong(userId), null, null, null, null, null, null, null);
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
