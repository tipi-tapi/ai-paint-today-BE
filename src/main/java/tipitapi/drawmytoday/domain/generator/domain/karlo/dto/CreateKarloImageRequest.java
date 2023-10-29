package tipitapi.drawmytoday.domain.generator.domain.karlo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest.KarloParameter;

/**
 * Request Body 정보:
 * https://developers.kakao.com/docs/latest/ko/karlo/rest-api#text-to-image-request-body
 */

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateKarloImageRequest {

    private final String prompt;
    private final String negativePrompt;
    private final String imageFormat;
    private final Integer samples;
    private final String returnType;
    private final Integer priorNumInferenceSteps;
    private final Double priorGuidanceScale;
    private final Integer numInferenceSteps;
    private final Double guidanceScale;
    private final String scheduler;
    private final Long[] seed;

    @Builder
    public CreateKarloImageRequest(String prompt, String negativePrompt, String imageFormat,
        Integer samples, String returnType, Integer priorNumInferenceSteps,
        Double priorGuidanceScale, Integer numInferenceSteps, Double guidanceScale,
        String scheduler, Long[] seed) {
        this.prompt = prompt;
        this.negativePrompt = negativePrompt;
        this.imageFormat = imageFormat;
        this.samples = samples;
        this.returnType = returnType;
        this.priorNumInferenceSteps = priorNumInferenceSteps;
        this.priorGuidanceScale = priorGuidanceScale;
        this.numInferenceSteps = numInferenceSteps;
        this.guidanceScale = guidanceScale;
        this.scheduler = scheduler;
        this.seed = seed;
    }

    public static CreateKarloImageRequest withUrl(String prompt) {
        return CreateKarloImageRequest.builder()
            .prompt(prompt)
            .imageFormat("webp")
            .samples(1)
            .returnType("url")
            .build();
    }

    public static CreateKarloImageRequest createTestRequest(KarloParameter param) {
        return CreateKarloImageRequest.builder()
            .prompt(param.getPrompt())
            .negativePrompt(param.getNegativePrompt())
            .imageFormat("webp")
            .samples(param.getSamples())
            .returnType("url")
            .priorNumInferenceSteps(param.getPriorNumInferenceSteps())
            .priorGuidanceScale(param.getPriorGuidanceScale())
            .numInferenceSteps(param.getNumInferenceSteps())
            .guidanceScale(param.getGuidanceScale())
            .scheduler(param.getScheduler())
            .seed(param.getSeed())
            .build();
    }
}
