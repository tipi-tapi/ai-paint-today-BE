package tipitapi.drawmytoday.domain.oauth.integrate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import tipitapi.drawmytoday.domain.oauth.properties.AppleClientSecret;
import tipitapi.drawmytoday.domain.oauth.properties.AppleProperties;

@SpringBootTest
public class ApplePropertiesTest {

    @Autowired
    AppleProperties appleProperties;
    @Autowired
    AppleClientSecret appleClientSecret;

    @Nested
    @DisplayName("getClientSecret 메서드는")
    class GetClientSecretMethod {

        @AfterEach
        void initializeAppleProperties() {
            ReflectionTestUtils.setField(appleClientSecret, "clientSecret", null);
        }

        @Test
        @DisplayName("처음 호출하면 null이 아닌 값을 반환한다")
        void when_call_first_then_not_return_null() {
            // given
            // when
            String clientSecret = appleProperties.getClientSecret();

            // then
            assertThat(clientSecret).isNotNull();
        }

        @Test
        @DisplayName("두 번째 호출시 기존 토큰이 만료되지 않았다면 기존 토큰을 반환한다")
        void call_twice_not_expired() {
            // given
            // when
            String firstToken = appleProperties.getClientSecret();
            String secondToken = appleProperties.getClientSecret();

            // then
            assertThat(firstToken).isEqualTo(secondToken);
        }

        @Test
        @DisplayName("두 번째 호출시 기존 토큰이 만료되었다면 새로운 토큰을 반환한다")
        void call_twice_expired() {
            // given
            // when
            String firstToken = appleProperties.getClientSecret();
            String expiredClientSecret = ReflectionTestUtils.invokeMethod(appleClientSecret,
                "generateClientSecret",
                appleProperties.getTeamId(), appleProperties.getKeyId(),
                appleProperties.getPrivateKey().getPrivateKey(),
                appleProperties.getClientId(), 0);
            ReflectionTestUtils.setField(appleClientSecret, "clientSecret",
                expiredClientSecret);
            String secondToken = appleProperties.getClientSecret();

            // then
            assertThat(firstToken).isNotEqualTo(secondToken);
        }
    }

}
