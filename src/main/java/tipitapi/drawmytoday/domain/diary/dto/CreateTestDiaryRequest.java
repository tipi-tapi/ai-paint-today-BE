package tipitapi.drawmytoday.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.validator.ValidDiaryDate;

@Getter
@Schema(description = "태스트 일기 생성 Request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateTestDiaryRequest {

    @NotNull
    @Schema(description = "감정 ID")
    private Long emotionId;

    @Size(max = 6010)
    @Schema(description = "일기 내용", nullable = true)
    private String notes;

    @NotNull
    @ValidDiaryDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @Schema(description = "일기 날짜")
    private LocalDate diaryDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @Schema(description = "현재 유저 시간", nullable = true, example = "12:00:00")
    private LocalTime userTime;

    @Valid
    private KarloParameter karloParameter;

    @Getter
    @AllArgsConstructor
    public static class KarloParameter {

        @NotBlank
        @Schema(description = "프롬프트", nullable = false)
        private String prompt;

        @Schema(description = "부정 프롬프트", nullable = true)
        private String negativePrompt;

        @Positive
        @NotNull
        @Schema(description = "이미지 개수. (양수여야 한다)", nullable = false)
        private Integer samples;

        @Schema(description = "이미지 생성 과정의 노이즈 제거 단계 수 (기본값: 25, 최소: 10, 최대 100)",
            nullable = true)
        private Integer priorNumInferenceSteps;

        @Schema(description = "이미지 생성 과정의 노이즈 제거 척도 (기본값: 5.0, 최소: 1.0, 최대: 20.0)",
            nullable = true)
        private Double priorGuidanceScale;

        @Schema(description = "각 이미지 생성 작업에 사용할 시드(Seed) 값. "
            + "생성할 이미지 수와 같은 길이의 배열이어야 함. 0 이상 4,294,967,295 이하 숫자로 구성", nullable = true)
        private Long[] seed;
    }

    public LocalTime getUserTime() {
        if (userTime == null) {
            return LocalTime.now();
        }
        return userTime;
    }
}
