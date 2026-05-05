package tipitapi.drawmytoday.common.entity;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public abstract class BaseEntity {

    private LocalDateTime createdAt;

    protected BaseEntity() {}

    protected BaseEntity(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}