package tipitapi.drawmytoday.domain.diary.repository;

import static tipitapi.drawmytoday.domain.diary.domain.QDiary.diary;
import static tipitapi.drawmytoday.domain.diary.domain.QImage.image;
import static tipitapi.drawmytoday.domain.diary.domain.QPrompt.prompt;
import static tipitapi.drawmytoday.domain.emotion.domain.QEmotion.emotion;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.support.PageableExecutionUtils;
import tipitapi.drawmytoday.domain.admin.dto.GetDiaryAdminResponse;
import tipitapi.drawmytoday.domain.admin.dto.GetDiaryNoteAndPromptResponse;
import tipitapi.drawmytoday.domain.admin.dto.QGetDiaryAdminResponse;
import tipitapi.drawmytoday.domain.admin.dto.QGetDiaryNoteAndPromptResponse;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.dto.GetMonthlyDiariesResponse;
import tipitapi.drawmytoday.domain.diary.dto.QGetMonthlyDiariesResponse;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;

@RequiredArgsConstructor
public class DiaryQueryRepositoryImpl implements DiaryQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<GetDiaryAdminResponse> getDiariesForMonitorAsPage(Pageable pageable,
        Direction direction, Long emotionId, boolean withTest) {

        BooleanExpression withoutTest = withTest ? null : diary.isTest.eq(false);
        BooleanExpression withEmotion = null;
        if (emotionId != null) {
            Emotion filterEmotion = queryFactory.selectFrom(emotion)
                .where(emotion.emotionId.eq(emotionId))
                .fetchOne();
            if (filterEmotion != null) {
                withEmotion = diary.emotion.eq(filterEmotion);
            }
        }

        List<GetDiaryAdminResponse> content = queryFactory.select(
                new QGetDiaryAdminResponse(diary.diaryId, image.imageUrl, prompt.promptText,
                    diary.createdAt, image.createdAt, image.review, diary.isTest))
            .from(image)
            .leftJoin(diary).on(diary.diaryId.eq(image.diary.diaryId))
            .leftJoin(prompt).on(image.prompt.promptId.eq(prompt.promptId))
            .where(withoutTest, withEmotion)
            .orderBy(direction.isAscending() ? image.createdAt.asc() : image.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(diary.count()).from(diary)
            .where(withoutTest, withEmotion);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Optional<Diary> getDiaryExistsByDiaryDate(Long userId, LocalDate diaryDate) {

        return Optional.ofNullable(queryFactory.selectFrom(diary)
            .where(diary.diaryDate.between(diaryDate.atStartOfDay(), diaryDate.atTime(23, 59, 59))
                .and(diary.user.userId.eq(userId)))
            .fetchFirst());
    }

    @Override
    public List<GetMonthlyDiariesResponse> getMonthlyDiaries(Long userId, LocalDateTime startMonth,
        LocalDateTime endMonth) {
        return queryFactory.select(
                new QGetMonthlyDiariesResponse(diary.diaryId, image.imageUrl.max(), diary.diaryDate))
            .from(diary)
            .leftJoin(image)
            .on(diary.diaryId.eq(image.diary.diaryId)
                .and(image.isSelected.eq(true)))
            .where(diary.diaryDate.between(startMonth, endMonth)
                .and(diary.user.userId.eq(userId)))
            .orderBy(diary.diaryDate.asc())
            .groupBy(diary.diaryId)
            .fetch();
    }

    @Override
    public List<GetDiaryNoteAndPromptResponse> getDiaryNoteAndPrompt() {
        return queryFactory.select(
                new QGetDiaryNoteAndPromptResponse(prompt.promptId, diary.notes, prompt.promptText))
            .from(prompt)
            .join(image).on(prompt.promptId.eq(image.prompt.promptId))
            .join(diary).on(image.diary.diaryId.eq(diary.diaryId))
            .where(prompt.promptGeneratorResult.promptGeneratorContent.isNull())
            .where(prompt.promptText.notLike("%, portrait"))
            .limit(10L)
            .fetch();
    }
}
