package com.ajogious.task_management_backend.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/uploads")
public class FileController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/users/{username}/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String username, @PathVariable String filename) {
        try {
            // Build the file path
            Path filePath = Paths.get(uploadDir).resolve("users").resolve(username).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            // Check if the file is readable
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Return the file as a response
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
