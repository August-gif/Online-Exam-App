 Online Exam App Server
 # status du projet : en developpement

Vue d'Ensemble du Projet
Ce projet est la partie backend (API RESTful) d'une application de gestion d'examens en ligne. Développé avec Spring Boot, il fournit toutes les fonctionnalités nécessaires pour créer, gérer et administrer des examens, ainsi que pour enregistrer les participations et les résultats des étudiants. L'application est conçue pour être robuste, sécurisée et flexible, supportant des fonctionnalités avancées comme les séries de questions (variantes d'examen) attribuées spécifiquement à chaque étudiant.

Fonctionnalités Clés
Gestion Complète des Examens : Création, lecture, mise à jour et suppression (CRUD) d'examens avec des détails comme le titre, la description, les dates de début/fin et la durée.

Gestion des Questions : Ajout et retrait de questions aux examens, avec support pour différents types de questions (choix multiples, vrai/faux, etc.). Chaque question peut être associée à une série d'examen (ex: "Série A", "Série B") ou être sans série.

Gestion des Étudiants : CRUD pour les profils étudiants.

Participations Étudiant-Examen :

Inscription : Permet d'inscrire un étudiant à un examen.

Démarrage de Session : Déclenche le début de la participation d'un étudiant à un examen, attribue une série de questions spécifique si l'examen en utilise, et marque le temps de début.
Soumission : Enregistre la fin de la participation de l'étudiant et calcule son score final.

Calcul de Score :

Calcul dynamique du score maximal pour chaque examen en fonction des questions ajoutées.

Calcul du score obtenu par un étudiant pour un examen donné, en tenant compte des réponses soumises et de la série de questions qu'il a reçue.

Statuts d'Examen : Possibilité de lister les examens actifs, à venir ou terminés.

Architecture de l'Application

L'application suit une architecture en couches classique pour les applications Spring Boot :

Controller : Gère les requêtes HTTP entrantes et expose les endpoints de l'API REST.

Service : Contient la logique métier principale de l'application, coordonnant les opérations entre les différents composants.

Repository : Interagit directement avec la base de données via Spring Data JPA pour les opérations CRUD et les requêtes personnalisées.

Model (Entities) : Représente la structure des données de l'application (Exam, Student, Question, StudentExamParticipation, etc.).

Exceptions : Classes d'exception personnalisées pour gérer les erreurs spécifiques à l'application.

Un point clé de l'architecture est l'entité StudentExamParticipation, qui agit comme une table de jointure enrichie. Elle gère non seulement la relation Many-to-Many entre Student et Exam, mais stocke également des attributs spécifiques à cette participation, comme la serieAttribuee à l'étudiant pour un examen donné.

API RESTful : Une interface standardisée pour l'intégration avec une application frontend ou d'autres services.
Technologies Utilisées
Backend :

Java 17+

Spring Boot 3.x

Spring Data JPA (pour la persistance des données)

Hibernate (implémentation de JPA)

Lombok (pour réduire le code boilerplate)

Base de Données :

PostgreSQL ( pour la production)

H2 Database (pour le développement et les tests in-memory)

Outil de Build :

Maven

Tests :

JUnit 5

Mockito
