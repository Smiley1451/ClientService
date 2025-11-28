package org.example.clientservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;

@Service
@Slf4j
public class ImageService {

    private final Cloudinary cloudinary;

    public ImageService(@Value("${cloudinary.cloud_name}") String cloudName,
                        @Value("${cloudinary.api_key}") String apiKey,
                        @Value("${cloudinary.api_secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    public Mono<String> uploadImage(FilePart filePart) {
        return Mono.fromCallable(() -> {
                    File tempFile = Files.createTempFile("upload", filePart.filename()).toFile();

                    return tempFile;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(tempFile -> filePart.transferTo(tempFile)
                        .then(Mono.fromCallable(() -> {
                            Map uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.emptyMap());
                            tempFile.delete();
                            return (String) uploadResult.get("url");
                        }).subscribeOn(Schedulers.boundedElastic()))
                );
    }
}