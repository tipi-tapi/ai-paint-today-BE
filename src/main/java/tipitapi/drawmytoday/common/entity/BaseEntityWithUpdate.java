package tipitapi.drawmytoday.common.entity;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public abstract class BaseEntityWithUpdate extends BaseEntity {

    private LocalDateTime updatedAt;

    protected BaseEntityWithUpdate() {
        super();
    }

    protected BaseEntityWithUpdate(LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt);
        this.updatedAt = updatedAt;
    }
}