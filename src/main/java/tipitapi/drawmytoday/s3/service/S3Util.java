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

    public String getFullUri(String uri) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, uri);
    }
}
