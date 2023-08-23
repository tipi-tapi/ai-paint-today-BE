package tipitapi.drawmytoday.ticket.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUser;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tipitapi.drawmytoday.ticket.domain.Ticket;
import tipitapi.drawmytoday.ticket.domain.TicketType;
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

            ArgumentCaptor<List<Ticket>> ticketArgumentCaptor = ArgumentCaptor.forClass(List.class);
            verify(ticketRepository, times(1)).saveAll(ticketArgumentCaptor.capture());

            List<Ticket> tickets = ticketArgumentCaptor.getValue();
            assertEquals(tickets.size(), 7);
            assertThat(tickets).allMatch(ticket -> ticket.getTicketType() == TicketType.JOIN);
        }
    }

    @Nested
    @DisplayName("createTicketByAdReward 메소드 테스트")
    class CreateTicketByAdRewardTest {

        @Test
        @DisplayName("AD_REWARD 타입의 티켓을 등록해야합니다.")
        void it_creates_ad_reward_ticket() {
            User user = createUser();

            ticketService.createTicketByAdReward(user);

            ArgumentCaptor<Ticket> ticketArgumentCaptor = ArgumentCaptor.forClass(Ticket.class);
            verify(ticketRepository, times(1)).save(ticketArgumentCaptor.capture());
            assertEquals(ticketArgumentCaptor.getValue().getTicketType(), TicketType.AD_REWARD);
        }
    }
}
