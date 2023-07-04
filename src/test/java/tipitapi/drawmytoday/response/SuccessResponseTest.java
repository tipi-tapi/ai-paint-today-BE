package tipitapi.drawmytoday.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tipitapi.drawmytoday.common.response.SuccessResponse;

@DisplayName("SuccessResponse 클래스는")
public class SuccessResponseTest {

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("data값이")
    class If_data_is {

        @Test
        @DisplayName("null이 아니면 data 키를 생성한다.")
        void if_data_not_null_than_data_key() throws Exception {
            // given
            SuccessResponse<String> successResponse = SuccessResponse.of("data");

            // when
            String json = objectMapper.writeValueAsString(successResponse);

            // then
            assertThat(json).contains("\"data\":\"data\"");
        }

        @Test
        @DisplayName("빈 리스트이면 data에 빈 배열을 반환한다.")
        void if_data_is_empty_list_than_empty_array() throws Exception {
            // given
            SuccessResponse<List<String>> successResponse = SuccessResponse.of(List.of());

            // when
            String json = objectMapper.writeValueAsString(successResponse);

            // then
            assertThat(json).contains("\"data\":[]");
        }

        @Test
        @DisplayName("null이면 data 키를 생성하지 않는다.")
        void if_data_null_than_no_data_key() throws Exception {
            // given
            SuccessResponse<String> successResponse = SuccessResponse.of(null);

            // when
            String json = objectMapper.writeValueAsString(successResponse);

            // then
            assertThat(json).doesNotContain("\"data\":");
        }
    }

}
