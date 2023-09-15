package tipitapi.drawmytoday.domain.gallery.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.entity.BaseEntity;
import tipitapi.drawmytoday.domain.user.domain.User;

@Table(name = "painting_report")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaintingReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paintingReportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "painting_id", nullable = false)
    private Painting painting;

    @Column(name = "report_detail")
    private String reportDetail;

    @Builder(access = AccessLevel.PRIVATE)
    private PaintingReport(User user, Painting painting, String reportDetail) {
        this.user = user;
        this.painting = painting;
        this.reportDetail = reportDetail;
    }

    public static PaintingReport create(User user, Painting painting, String reportDetail) {
        return PaintingReport.builder()
            .user(user)
            .painting(painting)
            .reportDetail(reportDetail)
            .build();
    }
}
