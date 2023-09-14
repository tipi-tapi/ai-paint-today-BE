package tipitapi.drawmytoday.domain.gallery.repository;

import static tipitapi.drawmytoday.domain.diary.domain.QImage.image;
import static tipitapi.drawmytoday.domain.gallery.domain.QPainting.painting;
import static tipitapi.drawmytoday.domain.gallery.domain.QPaintingHeart.paintingHeart;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import tipitapi.drawmytoday.domain.gallery.dto.GetPaintingResponse;
import tipitapi.drawmytoday.domain.gallery.dto.QGetPaintingResponse;

@RequiredArgsConstructor
public class PaintingQueryRepositoryImpl implements PaintingQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<GetPaintingResponse> findAllByPopularity(Pageable pageable) {
        List<GetPaintingResponse> content = queryFactory
            .select(new QGetPaintingResponse(painting.title, image.imageUrl, painting.notes))
            .from(painting)
            .join(painting.image, image)
            .leftJoin(paintingHeart).on(painting.paintingId.eq(paintingHeart.painting.paintingId))
            .groupBy(painting)
            .orderBy(paintingHeart.count().desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(painting.count()).from(painting);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<GetPaintingResponse> findAllByRecent(Pageable pageable) {
        List<GetPaintingResponse> content = queryFactory
            .select(new QGetPaintingResponse(painting.title, image.imageUrl, painting.notes))
            .from(painting)
            .join(painting.image, image)
            .leftJoin(paintingHeart).on(painting.paintingId.eq(paintingHeart.painting.paintingId))
            .orderBy(painting.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(painting.count()).from(painting);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
