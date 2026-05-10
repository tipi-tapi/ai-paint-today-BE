package tipitapi.drawmytoday.domain.user.repository;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import org.springframework.stereotype.Component;
import tipitapi.drawmytoday.domain.user.domain.SocialCode;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.domain.UserRole;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserDocumentMapper {

    private static final String FIELD_USER_ID = "userId";
    public static final String FIELD_EMAIL = "email";
    private static final String FIELD_SOCIAL_CODE = "socialCode";
    private static final String FIELD_USER_ROLE = "userRole";
    private static final String FIELD_LAST_DIARY_DATE = "lastDiaryDate";
    private static final String FIELD_DELETED_AT = "deletedAt";
    private static final String FIELD_CREATED_AT = "createdAt";
    private static final String FIELD_UPDATED_AT = "updatedAt";
    private static final String FIELD_REFRESH_TOKEN = "refreshToken";
    private static final String FIELD_APPLE_ID_TOKEN = "appleIdToken";

    public Map<String, Object> toDocument(User user) {
        var doc = new HashMap<String, Object>();
        doc.put(FIELD_USER_ID, user.getUserId());
        doc.put(FIELD_EMAIL, user.getEmail());
        doc.put(FIELD_SOCIAL_CODE, user.getSocialCode() != null ? user.getSocialCode().name() : null);
        doc.put(FIELD_USER_ROLE, user.getUserRole() != null ? user.getUserRole().name() : null);
        doc.put(FIELD_LAST_DIARY_DATE, toTimestamp(user.getLastDiaryDate()));
        doc.put(FIELD_DELETED_AT, toTimestamp(user.getDeletedAt()));
        doc.put(FIELD_CREATED_AT, toTimestamp(user.getCreatedAt()));
        doc.put(FIELD_UPDATED_AT, toTimestamp(user.getUpdatedAt()));
        doc.put(FIELD_REFRESH_TOKEN, user.getRefreshToken());
        doc.put(FIELD_APPLE_ID_TOKEN, user.getAppleIdToken());
        return doc;
    }

    public User fromDocument(DocumentSnapshot snapshot) {
        Long userId = Long.parseLong(snapshot.getId());
        String email = snapshot.getString(FIELD_EMAIL);
        String socialCodeStr = snapshot.getString(FIELD_SOCIAL_CODE);
        SocialCode socialCode = socialCodeStr != null ? SocialCode.valueOf(socialCodeStr) : null;
        String roleStr = snapshot.getString(FIELD_USER_ROLE);
        UserRole userRole = roleStr != null ? UserRole.valueOf(roleStr) : null;
        LocalDateTime lastDiaryDate = toLocalDateTime(snapshot.getTimestamp(FIELD_LAST_DIARY_DATE));
        LocalDateTime deletedAt = toLocalDateTime(snapshot.getTimestamp(FIELD_DELETED_AT));
        LocalDateTime createdAt = toLocalDateTime(snapshot.getTimestamp(FIELD_CREATED_AT));
        LocalDateTime updatedAt = toLocalDateTime(snapshot.getTimestamp(FIELD_UPDATED_AT));
        User user = User.restore(userId, email, socialCode, userRole, lastDiaryDate, deletedAt, createdAt, updatedAt);
        user.setRefreshToken(snapshot.getString(FIELD_REFRESH_TOKEN));
        user.setAppleIdToken(snapshot.getString(FIELD_APPLE_ID_TOKEN));
        return user;
    }

    private Timestamp toTimestamp(LocalDateTime ldt) {
        if (ldt == null) return null;
        var instant = ldt.atZone(ZoneId.systemDefault()).toInstant();
        return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        if (ts == null) return null;
        return ts.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
