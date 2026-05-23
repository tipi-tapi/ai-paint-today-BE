package tipitapi.drawmytoday.domain.ticket.repository;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import tipitapi.drawmytoday.common.util.FirestoreIdUtils;
import tipitapi.drawmytoday.domain.ticket.domain.Ticket;
import tipitapi.drawmytoday.domain.ticket.domain.TicketType;
import tipitapi.drawmytoday.domain.user.domain.User;

@Component
public class TicketDocumentMapper {

    static final String FIELD_TICKET_ID = "ticketId";
    static final String FIELD_USER_ID = "userId";
    static final String FIELD_TICKET_TYPE = "ticketType";
    static final String FIELD_USED_AT = "usedAt";
    static final String FIELD_CREATED_AT = "createdAt";

    public Map<String, Object> toDocument(Ticket ticket) {
        var doc = new HashMap<String, Object>();
        doc.put(FIELD_TICKET_ID, FirestoreIdUtils.toStorageType(ticket.getTicketId()));
        doc.put(FIELD_USER_ID, FirestoreIdUtils.toStorageType(userId(ticket)));
        doc.put(FIELD_TICKET_TYPE, ticket.getTicketType() != null ? ticket.getTicketType().name() : null);
        doc.put(FIELD_USED_AT, toTimestamp(ticket.getUsedAt()));
        doc.put(FIELD_CREATED_AT, toTimestamp(ticket.getCreatedAt()));
        return doc;
    }

    public Ticket fromDocument(DocumentSnapshot snapshot) {
        String ticketId = FirestoreIdUtils.toDomainId(snapshot.get(FIELD_TICKET_ID), snapshot.getId());
        User user = toUser(snapshot.get(FIELD_USER_ID));
        String ticketTypeStr = snapshot.getString(FIELD_TICKET_TYPE);
        TicketType ticketType = ticketTypeStr != null ? TicketType.valueOf(ticketTypeStr) : null;
        LocalDateTime usedAt = toLocalDateTime(snapshot.get(FIELD_USED_AT));
        LocalDateTime createdAt = toLocalDateTime(snapshot.get(FIELD_CREATED_AT));
        return Ticket.restore(ticketId, user, ticketType, usedAt, createdAt);
    }

    private String userId(Ticket ticket) {
        if (ticket.getUser() == null) {
            return null;
        }
        return ticket.getUser().getUserId();
    }

    private User toUser(Object userId) {
        String parsed = FirestoreIdUtils.toDomainId(userId, null);
        if (parsed == null) {
            return null;
        }
        return User.restore(parsed, null, null, null, null, null, null, null);
    }

    static Object toFirestoreId(String id) {
        return FirestoreIdUtils.toStorageType(id);
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
