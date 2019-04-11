package io.github.ifris.files.web.rest;

import io.github.ifris.files.service.IStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UploadsController {

    List<String> files = new ArrayList<>();
    @Autowired
    private IStorageService fileStorageService;

    /**
     * GET /api/upload
     * <p>
     * Upload file from the client API
     */
    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {

        String message = "";

        try {
            fileStorageService.store(file);
            files.add(file.getOriginalFilename());
            message = "Looking good! The file too... You have uploaded " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "Well! That's embarrasing! We've failed to upload " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }


    /**
     * GET /api/files
     * <p>
     * load files from storage
     */
    @GetMapping("/files")
    public ResponseEntity<List<String>> getListOfFiles(Model ui) {

        List<String> fileNames = files.stream().map(fileName -> MvcUriComponentsBuilder.fromMethodName(UploadsController.class, "getFile", fileName).build().toString()).collect(Collectors.toList());

        return ResponseEntity.ok().body(fileNames);
    }


    /**
     * GET /api/files/{filename:.+}
     * <p>
     * load file from storage
     */
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = fileStorageService.loadFile(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
