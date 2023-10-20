package tipitapi.drawmytoday.domain.diary.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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

    @Nested
    @DisplayName("reviewImage 메서드는")
    class ReviewImageTest {

        @ParameterizedTest
        @ValueSource(strings = {"", "0", "6", "a"})
        @DisplayName("review 값이 없거나 1~5 사이의 숫자가 아니면 BAD_REQUEST 상태코드를 응답한다.")
        void invalid_request_body_then_return_400(String review) throws Exception {
            // given
            Long imageId = 1L;

            // when
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("review", review);
            String requestBody = objectMapper.writeValueAsString(requestMap);
            ResultActions result = mockMvc.perform(post(BASIC_URL + "/" + imageId + "/review")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"1", "2", "3", "4", "5"})
        @DisplayName("review에 1~5 사이의 값이 들어오면 diaryId에 해당하는 이미지를 리뷰한다.")
        void review_image(String review) throws Exception {
            // given
            Long imageId = 1L;

            // when
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("review", review);
            String requestBody = objectMapper.writeValueAsString(requestMap);
            ResultActions result = mockMvc.perform(post(BASIC_URL + "/" + imageId + "/review")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

            // then
            result.andExpect(status().isNoContent());
            verify(imageService).reviewImage(imageId, REQUEST_USER_ID, review);
        }
    }

    @Nested
    @DisplayName("setSelectedImage 메서드는")
    class SetSelectedImageTest {

        @Test
        @DisplayName("imageId에 해당하는 일기를 대표 설정한다.")
        void sets_image_selected() throws Exception {
            // given
            Long imageId = 1L;

            // when
            ResultActions result = mockMvc.perform(put(BASIC_URL + "/" + imageId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            result.andExpect(status().isNoContent());
            verify(imageService).setSelectedImage(REQUEST_USER_ID, imageId);
        }
    }
}
