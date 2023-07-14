package tipitapi.drawmytoday.common.controller;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tipitapi.drawmytoday.common.validator.CustomCollectionValidator;

@Import({CustomCollectionValidator.class})
@MockBean(JpaMetamodelMappingContext.class)
public abstract class ControllerTestSetup {

    protected static final long REQUEST_USER_ID = 1L;
    protected MockMvc mockMvc;
    protected MockMvc noSecurityMockMvc;

    // "yyyy-MM-dd'T'HH:mm:ss.SSS" 형식으로 변환
    protected static String parseLocalDateTime(LocalDateTime input) {
        String inputString = input.toString();
        if (inputString.length() <= 23) {
            return inputString;
        }
        return inputString.substring(0, 23);
    }

    @BeforeEach
    void setUp(WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .alwaysDo(MockMvcResultHandlers.print())
            .build();
        this.noSecurityMockMvc = MockMvcBuilders.webAppContextSetup(context)
            .alwaysDo(MockMvcResultHandlers.print())
            .build();
    }

}
