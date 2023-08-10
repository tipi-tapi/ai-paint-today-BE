package tipitapi.drawmytoday.ticket.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tipitapi.drawmytoday.ticket.domain.Ticket;
import tipitapi.drawmytoday.ticket.repository.TicketRepository;
import tipitapi.drawmytoday.user.domain.User;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    TicketRepository ticketRepository;
    @InjectMocks
    TicketService ticketService;

    @Nested
    @DisplayName("createTicketByJoin 메소드 테스트")
    class CreateTicketByJoinTest {

        @Test
        @DisplayName("7개의 JOIN 타입의 티켓을 등록해야합니다.")
        void it_creates_7_join_tickets() {
            User user = createUser();

            ticketService.createTicketByJoin(user);

            verify(ticketRepository, times(7)).save(any(Ticket.class));
        }
    }
}
