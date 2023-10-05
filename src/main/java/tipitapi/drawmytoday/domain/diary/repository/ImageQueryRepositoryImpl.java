package tipitapi.drawmytoday.domain.diary.repository;

import static tipitapi.drawmytoday.domain.diary.domain.QImage.image;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import tipitapi.drawmytoday.domain.diary.domain.Image;
import tipitapi.drawmytoday.domain.user.domain.User;

@RequiredArgsConstructor
public class ImageQueryRepositoryImpl implements ImageQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Image> findLatestByDiary(Long diaryId) {
        return queryFactory
            .selectFrom(image)
            .where(image.diary.diaryId.eq(diaryId).and(image.deletedAt.isNull()))
            .orderBy(image.createdAt.desc())
            .fetch();
    }

    @Override
    public Optional<Image> findImage(Long imageId) {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(image)
                .where(image.imageId.eq(imageId).and(image.deletedAt.isNull()))
                .fetchFirst());
    }

    @Override
    public Long countImage(Long diaryId) {
        return queryFactory
            .select(image.count())
            .from(image)
            .where(image.diary.diaryId.eq(diaryId).and(image.deletedAt.isNull()))
            .fetchOne();
    }

    @Override
    public List<Image> findByDiary(Long diaryId) {
        return queryFactory
            .selectFrom(image)
            .where(image.diary.diaryId.eq(diaryId).and(image.deletedAt.isNull()))
            .fetch();
    }

    @Override
    public Optional<Image> findByImageIdAndDiaryUser(Long imageId, User user) {
        return Optional.ofNullable(queryFactory
            .selectFrom(image)
            .where(image.imageId.eq(imageId)
                .and(image.diary.user.eq(user))
                .and(image.deletedAt.isNull()))
            .fetchFirst());
    }
}
