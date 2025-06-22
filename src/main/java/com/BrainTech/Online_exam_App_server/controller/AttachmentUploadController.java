package com.BrainTech.Online_exam_App_server.controller;

import com.BrainTech.Online_exam_App_server.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/attachments") //Endpoint plus gérérique
@RequiredArgsConstructor
@Slf4j
public class AttachmentUploadController {
    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadAttachment(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Le fichier est vide.");
        }
        try {
            String fileUrl = fileStorageService.storeFile(file);
            log.info("Fichier {} uploadé avec succès. URL: {}", file.getOriginalFilename(), fileUrl);
            return ResponseEntity.ok(fileUrl); // Retourne l'URL du fichier stocké
        } catch (IOException e) {
            log.error("Échec de l'upload du fichier {}: {}", file.getOriginalFilename(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec de l'upload du fichier: " + e.getMessage());
        }
    }

}
