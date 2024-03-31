package tipitapi.drawmytoday.domain.admin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import tipitapi.drawmytoday.domain.admin.dto.GetDiaryAdminResponse;
import tipitapi.drawmytoday.domain.admin.dto.GetDiaryNoteAndPromptResponse;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.domain.PromptGeneratorResult;
import tipitapi.drawmytoday.domain.diary.repository.PromptRepository;
import tipitapi.drawmytoday.domain.diary.service.AdminDiaryService;
import tipitapi.drawmytoday.domain.generator.api.gpt.domain.ChatCompletionsRole;
import tipitapi.drawmytoday.domain.generator.api.gpt.domain.Message;
import tipitapi.drawmytoday.domain.generator.api.gpt.dto.GptChatCompletionsRequest;
import tipitapi.drawmytoday.domain.generator.service.TranslateTextService;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final ValidateUserService validateUserService;
    private final AdminDiaryService adminDiaryService;
    private final TranslateTextService translateTextService;
    private final TransactionTemplate writeTransactionTemplate;
    private final PromptRepository promptRepository;
    private final ObjectMapper objectMapper;
    @Value("${openai.gpt.chat_completions_prompt}")
    private String gptChatCompletionsPrompt;

    @Transactional(readOnly = true)
    public Page<GetDiaryAdminResponse> getDiaries(Long userId, int size, int page,
        Direction direction, Long emotionId, boolean withTest) {
        validateUserService.validateAdminUserById(userId);
        return adminDiaryService.getDiaries(size, page, direction, emotionId, withTest);
    }

    public int addGptGeneratorContent(Long userId) {
        validateUserService.validateAdminUserById(userId);
        List<GetDiaryNoteAndPromptResponse> responses = adminDiaryService.getDiaryNoteAndPrompt();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(responses.size());
        for (int i = 0; i < responses.size(); i++) {
            GetDiaryNoteAndPromptResponse response = responses.get(i);
            int finalI = i;
            executor.execute(() -> {
                try {
                    String translatedNotes = translateTextService.translateAutoToEnglish(
                        response.getNotes());
                    response.updateNotes(translatedNotes);
                } catch (Exception e) {
                    log.error("번역 API 예외가 발생했습니다.", e);
                    responses.remove(finalI);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("작업이 중단되었습니다.", e);
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }

        if (responses.isEmpty()) {
            throw new RuntimeException("번역할 데이터가 없거나 모두 실패했습니다.");
        }

        writeTransactionTemplate.executeWithoutResult(status -> {
            for (GetDiaryNoteAndPromptResponse response : responses) {
                Prompt prompt = promptRepository.findById(response.getPromptId()).get();
                List<Message> messages = GptChatCompletionsRequest.createFirstMessage(
                        gptChatCompletionsPrompt, response.getNotes())
                    .getMessages();
                messages.add(new Message(ChatCompletionsRole.assistant, response.getGptPrompt()));
                String gptResponses;
                try {
                    gptResponses = objectMapper.writeValueAsString(messages);
                } catch (JsonProcessingException e) {
                    log.error("GPT Message를 JSON으로 변환하는데 실패했습니다.", e);
                    throw new RuntimeException(e);
                }
                PromptGeneratorResult result = PromptGeneratorResult.createGpt3Result(gptResponses);
                prompt.updatePromptGeneratorResult(result);
            }
        });
        return responses.size();
    }
}
