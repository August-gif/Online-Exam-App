package com.BrainTech.Online_exam_App_server.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// @ResponseStatus indique que cette exception doit retourner un code HTTP 500 (Internal Server Error)
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class StorageException extends RuntimeException{
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}


/*
*Considérations Additionnelles :
Sécurité : Si tu optes pour le stockage local, assure-toi que le répertoire d'upload n'est pas accessible directement via des chemins arbitraires pour des raisons de sécurité. Pour le stockage Cloud, gère les permissions d'accès avec soin.
Validation : Valide toujours les fichiers uploadés (taille, type de fichier, etc.) côté backend pour prévenir les attaques.
Compression/Redimensionnement : Pour les performances de chargement du front-end, tu pourrais envisager de redimensionner ou de compresser les images après l'upload. Des services comme Cloudinary ou des librairies Java (Thumbnails, ImageIO) peuvent t'aider.
Nommage des fichiers : Utilise des noms de fichiers uniques (UUID est une bonne option) lors du stockage pour éviter les conflits et améliorer la sécurité.
Serveur de fichiers statiques : Si tu stockes localement, assure-toi que ton serveur web (Spring Boot embarqué ou un Nginx/Apache devant) est configuré pour servir les fichiers statiques depuis le répertoire d'upload
 */
