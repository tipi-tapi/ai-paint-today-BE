package tipitapi.drawmytoday.domain.ticket.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
import tipitapi.drawmytoday.domain.ticket.domain.Ticket;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FirestoreTicketRepository implements TicketRepository {

    private static final String USERS_COLLECTION = "users";
    private static final String TICKETS_COLLECTION = "tickets";
    private static final int BATCH_LIMIT = 400;
    private static final long TIMEOUT_SECONDS = 5L;

    private final Firestore firestore;
    private final TicketDocumentMapper mapper;

    @Override
    public Ticket save(Ticket ticket) {
        try {
            Long userId = requireUserId(ticket);
            Ticket target = ticket.getTicketId() != null ? ticket : restoreNewTicket(ticket);
            ticketCollection(userId)
                .document(String.valueOf(target.getTicketId()))
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
    public List<Ticket> saveAll(List<Ticket> tickets) {
        try {
            var saved = new ArrayList<Ticket>();
            for (var partition : partition(tickets, BATCH_LIMIT)) {
                WriteBatch batch = firestore.batch();
                for (var ticket : partition) {
                    Long userId = requireUserId(ticket);
                    Ticket target = ticket.getTicketId() != null ? ticket : restoreNewTicket(ticket);
                    batch.set(
                        ticketCollection(userId).document(String.valueOf(target.getTicketId())),
                        mapper.toDocument(target)
                    );
                    saved.add(target);
                }
                batch.commit().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            }
            return saved;
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
    public Optional<Ticket> findByTicketId(Long ticketId) {
        try {
            var documents = firestore.collectionGroup(TICKETS_COLLECTION)
                .whereEqualTo(TicketDocumentMapper.FIELD_TICKET_ID, ticketId)
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
    public List<Ticket> findAllByUserId(Long userId) {
        try {
            return ticketCollection(userId)
                .get()
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .getDocuments()
                .stream()
                .map(mapper::fromDocument)
                .sorted(Comparator.comparing(Ticket::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
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
    public List<Ticket> findAllByUserIdAndUsedAtIsNull(Long userId) {
        try {
            return ticketCollection(userId)
                .whereEqualTo(TicketDocumentMapper.FIELD_USED_AT, null)
                .get()
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .getDocuments()
                .stream()
                .map(mapper::fromDocument)
                .sorted(Comparator.comparing(Ticket::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
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
    public Optional<Ticket> findValidTicket(Long userId) {
        return findAllByUserIdAndUsedAtIsNull(userId).stream().findFirst();
    }

    private com.google.cloud.firestore.CollectionReference ticketCollection(Long userId) {
        return firestore.collection(USERS_COLLECTION)
            .document(String.valueOf(userId))
            .collection(TICKETS_COLLECTION);
    }

    private Long requireUserId(Ticket ticket) {
        if (ticket.getUser() == null || ticket.getUser().getUserId() == null) {
            throw new BusinessException(ErrorCode.FIRESTORE_IO_ERROR);
        }
        return ticket.getUser().getUserId();
    }

    private Ticket restoreNewTicket(Ticket ticket) {
        return Ticket.restore(
            generateTicketId(),
            ticket.getUser(),
            ticket.getTicketType(),
            ticket.getUsedAt(),
            ticket.getCreatedAt() != null ? ticket.getCreatedAt() : LocalDateTime.now()
        );
    }

    private Long generateTicketId() {
        return System.currentTimeMillis() * 1000L + ThreadLocalRandom.current().nextInt(1000);
    }

    private static <T> List<List<T>> partition(List<T> list, int size) {
        var result = new ArrayList<List<T>>();
        for (int i = 0; i < list.size(); i += size) {
            result.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return result;
    }
}
