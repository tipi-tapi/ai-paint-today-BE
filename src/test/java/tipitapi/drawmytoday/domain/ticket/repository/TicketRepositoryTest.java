package tipitapi.drawmytoday.domain.ticket.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import tipitapi.drawmytoday.common.BaseRepositoryTest;
import tipitapi.drawmytoday.common.config.QuerydslConfig;
import tipitapi.drawmytoday.domain.ticket.domain.Ticket;
import tipitapi.drawmytoday.domain.ticket.domain.TicketType;
import tipitapi.drawmytoday.domain.user.domain.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(QuerydslConfig.class)
public class TicketRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Nested
    @DisplayName("findValidTicket 메소드 테스트")
    class FindValidTicketTest {

        @Nested
        @DisplayName("등록된 티켓이 없을 경우")
        class If_no_ticket_exists {

            @Test
            @DisplayName("null을 반환한다.")
            void return_null() {
                User user = createUser();

                Optional<Ticket> ticket = ticketRepository.findValidTicket(user.getUserId());

                assertThat(ticket).isEmpty();
            }
        }

        @Nested
        @DisplayName("등록된 티켓이 사용된 상태인 경우")
        class If_ticket_is_used {

            @Test
            @DisplayName("null을 반환한다.")
            void return_null() {
                User user = createUser();
                Ticket ticket = Ticket.of(user, TicketType.JOIN);
                ReflectionTestUtils.setField(ticket, "usedAt", LocalDateTime.now().minusDays(1));
                ticketRepository.save(ticket);

                Optional<Ticket> result = ticketRepository.findValidTicket(user.getUserId());

                assertThat(result).isEmpty();
            }
        }

        @Nested
        @DisplayName("유효한 티켓이 존재하는 경우")
        class If_valid_ticket_exists {

            @Nested
            @DisplayName("여러개가 존재할 경우")
            class If_multiple_valid_ticket_exists {

                @Test
                @DisplayName("가장 오래된 티켓을 반환한다.")
                @Sql("ValidTicket.sql")
                void return_oldest_ticket() {
                    final Long userId = 1L;

                    Optional<Ticket> ticket = ticketRepository.findValidTicket(userId);

                    assertThat(ticket.isPresent()).isTrue();
                    assertThat(ticket.get().getTicketId()).isEqualTo(2L);
                }
            }
        }
    }

}
