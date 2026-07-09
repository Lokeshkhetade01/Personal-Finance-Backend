package com.pfm.financemanager.serviceImpl;

import com.pfm.financemanager.exception.FileStorageException;
import com.pfm.financemanager.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "pdf");

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    @Override
    public String storeReceiptFile(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("Receipt file must not be empty");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String extension = getExtension(originalFileName);

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new FileStorageException("Only jpg, jpeg, png and pdf files are allowed");
        }

        String fileName = "receipt_" + userId + "_" + UUID.randomUUID() + "." + extension;

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new FileStorageException("Could not store receipt file " + fileName, e);
        }
    }

    private String getExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1 || lastDot == fileName.length() - 1) {
            throw new FileStorageException("File must have a valid extension");
        }
        return fileName.substring(lastDot + 1);
    }
}
