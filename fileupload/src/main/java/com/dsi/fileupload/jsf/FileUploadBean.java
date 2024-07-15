package com.dsi.fileupload.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.primefaces.model.file.UploadedFile;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Named
@ViewScoped
public class FileUploadBean implements Serializable {

    private UploadedFile file;

    // MinIO configuration
    private final String minioUrl = "http://192.168.122.224:9000"; // Replace with your MinIO URL
    private final String accessKey = "minioadmin"; // Replace with your access key
    private final String secretKey = "minioadmin"; // Replace with your secret key
    private final String bucketName = "scratchbucket"; // Replace with your bucket name

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public void upload() {
        if (file != null) {
            try {
                MinioClient minioClient = MinioClient.builder()
                        .endpoint(minioUrl)
                        .credentials(accessKey, secretKey)
                        .build();

                // Upload file to MinIO
                try (InputStream is = file.getInputStream()) {
                    minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(file.getFileName())
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
                }

                System.out.println("Uploaded file: " + file.getFileName());
            } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
                e.printStackTrace();
                System.out.println("Error uploading file: " + e.getMessage());
            }
        } else {
            System.out.println("No file selected.");
        }
    }
}
