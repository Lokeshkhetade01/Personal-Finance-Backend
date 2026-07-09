package com.pfm.financemanager.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storeReceiptFile(MultipartFile file, Long userId);
}
