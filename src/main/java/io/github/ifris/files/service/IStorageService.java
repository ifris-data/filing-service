package io.github.ifris.files.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for response to uploaded files
 */
public interface IStorageService {

    /**
     * Save a file
     */
    void store(MultipartFile file);

    /**
     * Load a file
     */
    Resource loadFile(String filename);

    /**
     * Delete all uploaded files
     */
    void deleteAll();

    /**
     * Create upload directory
     */
    void init();
}
