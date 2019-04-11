package io.github.ifris.files.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service("fileStorageService")
public class FileStorageService implements IStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final Path rootLocation = Paths.get("uploads");

    /**
     * Save a file
     */
    @Override
    public void store(MultipartFile file) {

        log.info("The system is storing file by the name : {}", file.getOriginalFilename());

        try {
            Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file!");
        }

    }

    /**
     * Load a file
     */
    @Override
    public Resource loadFile(String filename) {
        log.info("Fetching from storage file : {}", filename);

        Path file = rootLocation.resolve(filename);

        try {
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("The requested file does not exist or is unreadable");
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL bro!");
        }
    }

    /**
     * Delete all uploaded files
     */
    @Override
    public void deleteAll() {

        log.info("Deleting all uploaded files .. .");

        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    /**
     * Create upload directory
     */
    @Override
    public void init() {

        log.info("Creating file uploads directory ... ");

        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage. Kindly check if you have read/write permissions");
        }

    }
}
