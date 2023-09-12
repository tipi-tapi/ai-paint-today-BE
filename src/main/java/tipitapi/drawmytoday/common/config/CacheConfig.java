package tipitapi.drawmytoday.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tipitapi.drawmytoday.common.cache.CacheType;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        List<CaffeineCache> caches = Arrays.stream(CacheType.values())
            .map(
                cache -> new CaffeineCache(cache.getCacheName(), Caffeine.newBuilder().recordStats()
                    .expireAfterWrite(cache.getExpireAfterWrite(), TimeUnit.SECONDS)
                    .maximumSize(cache.getMaximumSize())
                    .build()
                )
            )
            .collect(Collectors.toList());

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return cacheManager;
    }
}
