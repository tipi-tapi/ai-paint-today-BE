package tipitapi.drawmytoday.domain.diary.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import tipitapi.drawmytoday.common.controller.ControllerTestSetup;
import tipitapi.drawmytoday.common.controller.WithCustomUser;
import tipitapi.drawmytoday.domain.diary.service.ImageService;

@WebMvcTest(ImageController.class)
@WithCustomUser
class ImageControllerTest extends ControllerTestSetup {

    private static final String BASIC_URL = "/image";

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private ImageService imageService;

    @Nested
    @DisplayName("deleteImage 메서드는")
    class DeleteImageTest {

        @Test
        @DisplayName("imageId에 해당하는 일기를 삭제한다.")
        void delete_image() throws Exception {
            // given
            Long imageId = 1L;

            // when
            ResultActions result = mockMvc.perform(delete(BASIC_URL + "/" + imageId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            result.andExpect(status().isNoContent());
            verify(imageService).deleteImage(imageId, REQUEST_USER_ID);
        }
    }
}