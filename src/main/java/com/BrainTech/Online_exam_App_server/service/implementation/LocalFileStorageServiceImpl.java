package com.BrainTech.Online_exam_App_server.service.implementation;

import com.BrainTech.Online_exam_App_server.exceptions.StorageException;
import com.BrainTech.Online_exam_App_server.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;


@Service // Indique à Spring que c'est un composant à gérer
@Slf4j   // Pour la journalisation
public class LocalFileStorageServiceImpl implements FileStorageService {

    // Chemin du répertoire où les fichiers seront stockés sur le VPS.
    // Cette valeur est injectée depuis application.properties ou application.yml.
    // La valeur par défaut 'uploads' signifie un dossier 'uploads' dans le répertoire courant de l'application.
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // Cette méthode est exécutée juste après la construction du bean
    // Elle s'assure que le répertoire d'upload existe. Si ce n'est pas le cas, elle tente de le créer.

    @PostConstruct
    public void init() {
        try {
            // Crée le répertoire s'il n'existe pas. Les parents seront aussi créés si besoin.
            Files.createDirectories(Paths.get(uploadDir));
            log.info("Répertoire d'upload créé ou déjà existant: {}", uploadDir);
        } catch (IOException e) {
            log.error("Impossible de créer le répertoire d'upload: {}", uploadDir, e);
            // Si le répertoire ne peut pas être créé, c'est une erreur critique.
            // L'application ne pourra pas fonctionner correctement.
            throw new StorageException("Impossible d'initialiser le répertoire de stockage", e);
        }
    }
    @Override
    public String storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new StorageException("Impossible de stocker un fichier vide.");
        }
        // Nettoie le nom de fichier pour éviter les injections de chemin (ex: "../../../malicious.txt")
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileName = System.currentTimeMillis() + "_" + originalFilename; // Rend le nom unique

        if (fileName.contains("..")) {
            // Mesure de sécurité : ne pas stocker des chemins relatifs.
            throw new StorageException("Nom de fichier invalide: " + fileName);
        }

        try {
            Path targetLocation = Paths.get(uploadDir).resolve(fileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }
            log.info("Fichier '{}' stocké localement à: {}", fileName, targetLocation.toAbsolutePath());

            // Retourne un chemin relatif que le front-end utilisera.
            // Il est crucial de configurer le serveur web (ou Spring Boot) pour servir ce répertoire.
            return "/files/" + fileName; // Exemple d'URL accessible publiquement
        } catch (IOException e) {
            log.error("Échec du stockage du fichier '{}': {}", fileName, e.getMessage());
            throw new StorageException("Échec du stockage du fichier " + fileName, e);
        }
    }

    /**
     * Supprime un fichier du système de fichiers local du VPS.
     *
     * @param fileUrl L'URL/chemin du fichier à supprimer, tel que retourné par storeFile.
     * @throws StorageException Si le chemin est invalide ou une erreur d'E/S survient.
     */
    @Override
    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return; // Rien à supprimer
        }
        // Extrait le nom de fichier de l'URL relative (ex: "/files/mon_fichier.pdf" -> "mon_fichier.pdf")
        String fileName = fileUrl.replaceFirst("/files/", "");
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Fichier '{}' supprimé du stockage local.", filePath.toAbsolutePath());
            } else {
                log.warn("Tentative de suppression d'un fichier inexistant: {}", filePath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Échec de la suppression du fichier '{}': {}", fileName, e.getMessage());
            throw new StorageException("Échec de la suppression du fichier " + fileName, e);
        }

    }
}
