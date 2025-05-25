# Mini-CI/CD

Une application simplifiée de CI/CD qui permet d'enregistrer des dépôts GitLab, de déclencher des builds Docker, et de mémoriser le statut et la date des builds via une interface web server-side.

## Fonctionnalités

- Enregistrement de dépôts GitLab
- Déclenchement de builds Docker (clone + mvn clean package)
- Mémorisation du statut et de la date des builds
- Interface web server-side avec Thymeleaf et Bootstrap 5

## Stack technique

- **Backend** : Spring Boot 3 (Java 21), Spring MVC, Spring Data JPA, PostgreSQL
- **Frontend** : Thymeleaf 3 + Bootstrap 5
- **Runner** : Script shell dans un conteneur Maven officiel
- **Base de données** : PostgreSQL 15

## Endpoints

| Action | Route REST / page HTML |
|--------|------------------------|
| Lister projets | **GET /projects** → table HTML |
| Ajouter projet | **POST /projects** (formulaire simple) |
| Lancer build | **POST /builds/{projectId}** (bouton « Build » dans la liste) |
| Voir historique | **GET /builds** → table des 10 derniers builds |

## Prérequis

- Java 21
- Docker et Docker Compose
- Maven (optionnel, wrapper inclus)

## Installation et démarrage

1. Cloner le dépôt
2. Lancer l'application avec Docker Compose :

```bash
docker-compose up -d
```

3. Accéder à l'application via http://localhost:8080

## Développement

Pour lancer l'application en mode développement :

```bash
./mvnw spring-boot:run
```

## Structure du projet

- `src/main/java/com/cicd/demo/model` : Entités JPA (Project, Build)
- `src/main/java/com/cicd/demo/repository` : Repositories Spring Data JPA
- `src/main/java/com/cicd/demo/service` : Services métier
- `src/main/java/com/cicd/demo/controller` : Contrôleurs Spring MVC
- `src/main/resources/templates` : Templates Thymeleaf
- `src/main/resources/scripts` : Scripts pour le runner Docker

## Conteneurisation

L'application est conteneurisée avec Docker et peut être déployée avec Docker Compose. Le fichier `compose.yaml` définit deux services :

- `db` : Base de données PostgreSQL
- `api` : Application Spring Boot
