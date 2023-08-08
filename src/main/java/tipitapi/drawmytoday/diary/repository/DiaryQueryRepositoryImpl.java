package tipitapi.drawmytoday.diary.repository;

import static tipitapi.drawmytoday.diary.domain.QDiary.diary;
import static tipitapi.drawmytoday.diary.domain.QImage.image;
import static tipitapi.drawmytoday.diary.domain.QPrompt.prompt;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.support.PageableExecutionUtils;
import tipitapi.drawmytoday.admin.dto.GetDiaryAdminResponse;
import tipitapi.drawmytoday.admin.dto.QGetDiaryAdminResponse;

@RequiredArgsConstructor
public class DiaryQueryRepositoryImpl implements DiaryQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<GetDiaryAdminResponse> getDiariesForMonitorAsPage(Pageable pageable,
        Direction direction) {
        List<GetDiaryAdminResponse> content = queryFactory.select(
                new QGetDiaryAdminResponse(diary.diaryId, image.imageUrl, prompt.promptText,
                    diary.createdAt))
            .from(diary)
            .leftJoin(image).on(diary.diaryId.eq(image.diary.diaryId))
            .leftJoin(prompt).on(diary.diaryId.eq(prompt.diary.diaryId))
            .where(diary.isTest.eq(false))
            .orderBy(direction.isAscending() ? diary.createdAt.asc() : diary.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(diary.count()).from(diary)
            .where(diary.isTest.eq(false));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

}
