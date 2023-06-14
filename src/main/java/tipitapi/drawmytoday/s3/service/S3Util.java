package tipitapi.drawmytoday.s3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class S3Util {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    /*
     * 주어진 S3 URI의 Full S3 URL.
     * 현재는 버킷의 퍼블릭 액세스 사용을 허용하지 않으므로, 이 URL을 사용해도 접근할 수 없다.
     */
    @Deprecated
    public String getFullUri(String uri) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, uri);
    }
}
