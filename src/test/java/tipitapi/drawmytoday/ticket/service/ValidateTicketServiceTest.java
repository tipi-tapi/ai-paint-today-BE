package tipitapi.drawmytoday.ticket.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUserWithId;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tipitapi.drawmytoday.ticket.domain.Ticket;
import tipitapi.drawmytoday.ticket.domain.TicketType;
import tipitapi.drawmytoday.ticket.repository.TicketRepository;
import tipitapi.drawmytoday.user.domain.User;

@ExtendWith(MockitoExtension.class)
public class ValidateTicketServiceTest {

    @Mock
    TicketRepository ticketRepository;
    @InjectMocks
    ValidateTicketService validateTicketService;

    @Nested
    @DisplayName("findValidTicket 메소드 테스트")
    class FindValidTicketTest {

        @Nested
        @DisplayName("유효한 티켓이 존재하지 않을 경우")
        class If_no_valid_ticket_exists {

            @Test
            @DisplayName("null를 반환한다.")
            void return_null() {
                given(ticketRepository.findValidTicket(anyLong())).willReturn(Optional.empty());

                Optional<Ticket> ticket = validateTicketService.findValidTicket(1L);

                assertThat(ticket).isEmpty();
            }
        }

        @Nested
        @DisplayName("유효한 티켓이 존재할 경우")
        class If_valid_ticket_exists {

            @Test
            @DisplayName("티켓을 반환한다.")
            void return_ticket() {
                User user = createUserWithId(1L);
                Ticket ticket = Ticket.of(user, TicketType.JOIN);
                given(ticketRepository.findValidTicket(anyLong())).willReturn(Optional.of(ticket));

                Optional<Ticket> result = validateTicketService.findValidTicket(user.getUserId());

                assertThat(result).isPresent();
                assertThat(result.get()).isEqualTo(ticket);
            }
        }
    }
}
