package com.BrainTech.Online_exam_App_server.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    /**
     * Stocke un fichier et retourne son URL/chemin d'accès.
     * @param file Le fichier à stocker.
     * @return L'URL ou le chemin d'accès au fichier stocké.
     * @throws IOException En cas d'erreur de stockage.
     */
    String storeFile(MultipartFile file) throws IOException;

    /**
     * Supprime un fichier à partir de son URL/chemin d'accès.
     * @param fileUrl L'URL ou le chemin du fichier à supprimer.
     * @throws IOException En cas d'erreur de suppression.
     */
    void deleteFile(String fileUrl) throws IOException;

    // Tu pourrais aussi vouloir une méthode pour générer une URL temporaire/sécurisée
    // si tu utilises des services cloud avec des buckets privés.
    // String generateDownloadUrl(String fileUrl);

}
