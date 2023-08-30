package tipitapi.drawmytoday.common.cache;

import lombok.Getter;

@Getter
public enum CacheType {
    ACTIVE_EMOTIONS(
        CacheConst.ACTIVE_EMOTIONS,
        3 * 60 * 60,
        10
    );

    CacheType(String cacheName, int expireAfterWrite, int maximumSize) {
        this.cacheName = cacheName;
        this.expireAfterWrite = expireAfterWrite;
        this.maximumSize = maximumSize;
    }

    private final String cacheName;
    private final int expireAfterWrite;
    private final int maximumSize;

}
