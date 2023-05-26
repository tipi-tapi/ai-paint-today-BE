package tipitapi.drawmytoday.s3.service;

import java.io.InputStream;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public void uploadFromUrl(String imageUrl, String filePath) throws Exception {
        try (InputStream inputStream = new URL(imageUrl).openStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName)
                .key(filePath).build();
            s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(inputStream, inputStream.available()));
        }
    }

    public void uploadFromBase64(byte[] imageBytes, String filePath) throws Exception {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName)
            .key(filePath).build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));
    }

}
