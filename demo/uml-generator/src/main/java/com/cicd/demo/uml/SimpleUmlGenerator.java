package com.cicd.demo.uml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Générateur simple de diagrammes UML pour le projet Mini-CI/CD.
 * Cette classe génère des diagrammes UML prédéfinis sans analyser le code source.
 */
public class SimpleUmlGenerator {

    public static void main(String[] args) {
        System.out.println("Démarrage du générateur UML simplifié pour Mini-CI/CD");

        // Définir le répertoire de sortie
        String outputDir = "diagrams";
        File outputDirFile = new File(outputDir);
        if (!outputDirFile.exists()) {
            if (outputDirFile.mkdirs()) {
                System.out.println("Répertoire de sortie créé : " + outputDir);
            } else {
                System.err.println("Impossible de créer le répertoire de sortie : " + outputDir);
                return;
            }
        }

        try {
            // Générer les diagrammes UML
            generateClassDiagram(outputDir);
            generateUseCaseDiagram(outputDir);
            generateSequenceDiagram(outputDir);

            System.out.println("Génération des diagrammes UML terminée avec succès");
            System.out.println("Les fichiers PlantUML sont disponibles dans le répertoire : " + outputDir);
            System.out.println("Pour générer les images, utilisez l'outil PlantUML : https://plantuml.com/fr/");
        } catch (IOException e) {
            System.err.println("Erreur lors de la génération des diagrammes UML : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Génère le diagramme de classes UML.
     *
     * @param outputDir Le répertoire de sortie
     * @throws IOException En cas d'erreur lors de la génération du diagramme
     */
    private static void generateClassDiagram(String outputDir) throws IOException {
        System.out.println("Génération du diagramme de classes...");

        Path plantUmlFile = Paths.get(outputDir, "class-diagram.puml");
        
        try (FileWriter writer = new FileWriter(plantUmlFile.toFile())) {
            writer.write("@startuml\n");
            writer.write("!theme plain\n");
            writer.write("skinparam classAttributeIconSize 0\n");
            writer.write("skinparam classFontStyle bold\n");
            writer.write("title Diagramme de Classes - Mini-CI/CD\n\n");

            // Package model
            writer.write("package \"com.cicd.demo.model\" {\n");
            writer.write("  class Project {\n");
            writer.write("    - id: Long\n");
            writer.write("    - name: String\n");
            writer.write("    - repositoryUrl: String\n");
            writer.write("    - branch: String\n");
            writer.write("    - createdAt: LocalDateTime\n");
            writer.write("    - builds: List<Build>\n");
            writer.write("    + onCreate(): void\n");
            writer.write("  }\n\n");

            writer.write("  class Build {\n");
            writer.write("    - id: Long\n");
            writer.write("    - project: Project\n");
            writer.write("    - status: BuildStatus\n");
            writer.write("    - logs: String\n");
            writer.write("    - startTime: LocalDateTime\n");
            writer.write("    - endTime: LocalDateTime\n");
            writer.write("    + onCreate(): void\n");
            writer.write("    + isCompleted(): boolean\n");
            writer.write("  }\n\n");

            writer.write("  enum BuildStatus {\n");
            writer.write("    PENDING\n");
            writer.write("    RUNNING\n");
            writer.write("    SUCCESS\n");
            writer.write("    FAILURE\n");
            writer.write("  }\n");
            writer.write("}\n\n");

            // Package controller
            writer.write("package \"com.cicd.demo.controller\" {\n");
            writer.write("  class BuildController {\n");
            writer.write("    - buildService: BuildService\n");
            writer.write("    - projectService: ProjectService\n");
            writer.write("    + listBuilds(): String\n");
            writer.write("    + listSuccessBuilds(): String\n");
            writer.write("    + listFailureBuilds(): String\n");
            writer.write("    + viewBuild(id): String\n");
            writer.write("    + triggerBuild(projectId): String\n");
            writer.write("  }\n\n");

            writer.write("  class ProjectController {\n");
            writer.write("    - projectService: ProjectService\n");
            writer.write("    - buildService: BuildService\n");
            writer.write("    + listProjects(): String\n");
            writer.write("    + viewProject(id): String\n");
            writer.write("    + showCreateForm(): String\n");
            writer.write("    + createProject(project): String\n");
            writer.write("    + deleteProject(id): String\n");
            writer.write("  }\n\n");

            writer.write("  class DashboardController {\n");
            writer.write("    - projectService: ProjectService\n");
            writer.write("    - buildService: BuildService\n");
            writer.write("    + dashboard(): String\n");
            writer.write("  }\n");
            writer.write("}\n\n");

            // Package service
            writer.write("package \"com.cicd.demo.service\" {\n");
            writer.write("  interface BuildService {\n");
            writer.write("    + getAllBuilds(): List<Build>\n");
            writer.write("    + getRecentBuilds(limit): List<Build>\n");
            writer.write("    + getBuildsByProjectId(projectId): List<Build>\n");
            writer.write("    + getBuildById(id): Optional<Build>\n");
            writer.write("    + saveBuild(build): Build\n");
            writer.write("    + triggerBuild(projectId): Build\n");
            writer.write("  }\n\n");

            writer.write("  class BuildServiceImpl {\n");
            writer.write("    - buildRepository: BuildRepository\n");
            writer.write("    - projectService: ProjectService\n");
            writer.write("    - dockerImage: String\n");
            writer.write("    - dockerWorkdir: String\n");
            writer.write("    + getAllBuilds(): List<Build>\n");
            writer.write("    + getRecentBuilds(limit): List<Build>\n");
            writer.write("    + getBuildsByProjectId(projectId): List<Build>\n");
            writer.write("    + getBuildById(id): Optional<Build>\n");
            writer.write("    + saveBuild(build): Build\n");
            writer.write("    + triggerBuild(projectId): Build\n");
            writer.write("    - executeBuild(build): void\n");
            writer.write("    - prepareRunnerScript(): String\n");
            writer.write("  }\n\n");

            writer.write("  interface ProjectService {\n");
            writer.write("    + getAllProjects(): List<Project>\n");
            writer.write("    + getProjectById(id): Optional<Project>\n");
            writer.write("    + saveProject(project): Project\n");
            writer.write("    + deleteProject(id): void\n");
            writer.write("  }\n\n");

            writer.write("  class ProjectServiceImpl {\n");
            writer.write("    - projectRepository: ProjectRepository\n");
            writer.write("    + getAllProjects(): List<Project>\n");
            writer.write("    + getProjectById(id): Optional<Project>\n");
            writer.write("    + saveProject(project): Project\n");
            writer.write("    + deleteProject(id): void\n");
            writer.write("  }\n");
            writer.write("}\n\n");

            // Package repository
            writer.write("package \"com.cicd.demo.repository\" {\n");
            writer.write("  interface BuildRepository {\n");
            writer.write("    + findAllByOrderByStartTimeDesc(pageable): List<Build>\n");
            writer.write("    + findByProjectId(projectId): List<Build>\n");
            writer.write("  }\n\n");

            writer.write("  interface ProjectRepository {\n");
            writer.write("  }\n");
            writer.write("}\n\n");

            // Relations
            writer.write("Project \"1\" *-- \"*\" Build\n");
            writer.write("Build *-- BuildStatus\n");
            writer.write("BuildController ..> BuildService\n");
            writer.write("BuildController ..> ProjectService\n");
            writer.write("ProjectController ..> ProjectService\n");
            writer.write("ProjectController ..> BuildService\n");
            writer.write("DashboardController ..> ProjectService\n");
            writer.write("DashboardController ..> BuildService\n");
            writer.write("BuildServiceImpl ..|> BuildService\n");
            writer.write("BuildServiceImpl ..> BuildRepository\n");
            writer.write("BuildServiceImpl ..> ProjectService\n");
            writer.write("ProjectServiceImpl ..|> ProjectService\n");
            writer.write("ProjectServiceImpl ..> ProjectRepository\n");

            writer.write("@enduml\n");
        }

        System.out.println("Diagramme de classes généré : " + plantUmlFile);
    }

    /**
     * Génère le diagramme de cas d'utilisation UML.
     *
     * @param outputDir Le répertoire de sortie
     * @throws IOException En cas d'erreur lors de la génération du diagramme
     */
    private static void generateUseCaseDiagram(String outputDir) throws IOException {
        System.out.println("Génération du diagramme de cas d'utilisation...");

        Path plantUmlFile = Paths.get(outputDir, "use-case-diagram.puml");
        
        try (FileWriter writer = new FileWriter(plantUmlFile.toFile())) {
            writer.write("@startuml\n");
            writer.write("!theme plain\n");
            writer.write("skinparam actorStyle awesome\n");
            writer.write("title Diagramme de Cas d'Utilisation - Mini-CI/CD\n\n");

            // Définir les acteurs
            writer.write("actor \"Développeur\" as Dev\n");
            writer.write("actor \"Administrateur\" as Admin\n");
            writer.write("actor \"Système CI/CD\" as System\n\n");

            // Définir les cas d'utilisation
            writer.write("rectangle \"Mini-CI/CD\" {\n");
            
            // Gestion des projets
            writer.write("  package \"Gestion des Projets\" {\n");
            writer.write("    usecase \"Créer un projet\" as UC1\n");
            writer.write("    usecase \"Modifier un projet\" as UC2\n");
            writer.write("    usecase \"Supprimer un projet\" as UC3\n");
            writer.write("    usecase \"Consulter les projets\" as UC4\n");
            writer.write("  }\n\n");
            
            // Gestion des builds
            writer.write("  package \"Gestion des Builds\" {\n");
            writer.write("    usecase \"Déclencher un build\" as UC5\n");
            writer.write("    usecase \"Consulter les builds\" as UC6\n");
            writer.write("    usecase \"Consulter les builds réussis\" as UC7\n");
            writer.write("    usecase \"Consulter les builds échoués\" as UC8\n");
            writer.write("    usecase \"Consulter les logs d'un build\" as UC9\n");
            writer.write("  }\n\n");
            
            // Exécution des builds
            writer.write("  package \"Exécution des Builds\" {\n");
            writer.write("    usecase \"Cloner le dépôt Git\" as UC10\n");
            writer.write("    usecase \"Exécuter le build Maven\" as UC11\n");
            writer.write("    usecase \"Capturer les logs\" as UC12\n");
            writer.write("    usecase \"Mettre à jour le statut du build\" as UC13\n");
            writer.write("    usecase \"Créer un projet de démonstration\" as UC14\n");
            writer.write("  }\n\n");
            
            writer.write("}\n\n");

            // Relations entre acteurs et cas d'utilisation
            // Développeur
            writer.write("Dev --> UC1\n");
            writer.write("Dev --> UC2\n");
            writer.write("Dev --> UC3\n");
            writer.write("Dev --> UC4\n");
            writer.write("Dev --> UC5\n");
            writer.write("Dev --> UC6\n");
            writer.write("Dev --> UC7\n");
            writer.write("Dev --> UC8\n");
            writer.write("Dev --> UC9\n");
            
            // Administrateur
            writer.write("Admin --> UC1\n");
            writer.write("Admin --> UC2\n");
            writer.write("Admin --> UC3\n");
            writer.write("Admin --> UC4\n");
            writer.write("Admin --> UC5\n");
            writer.write("Admin --> UC6\n");
            writer.write("Admin --> UC7\n");
            writer.write("Admin --> UC8\n");
            writer.write("Admin --> UC9\n");
            
            // Système CI/CD
            writer.write("System --> UC10\n");
            writer.write("System --> UC11\n");
            writer.write("System --> UC12\n");
            writer.write("System --> UC13\n");
            writer.write("System --> UC14\n");
            
            // Relations entre cas d'utilisation
            writer.write("UC5 ..> UC10 : <<include>>\n");
            writer.write("UC5 ..> UC11 : <<include>>\n");
            writer.write("UC5 ..> UC12 : <<include>>\n");
            writer.write("UC5 ..> UC13 : <<include>>\n");
            writer.write("UC10 ..> UC14 : <<extend>>\n");
            writer.write("UC6 <.. UC7 : <<extend>>\n");
            writer.write("UC6 <.. UC8 : <<extend>>\n");
            
            writer.write("@enduml\n");
        }

        System.out.println("Diagramme de cas d'utilisation généré : " + plantUmlFile);
    }

    /**
     * Génère le diagramme de séquence UML.
     *
     * @param outputDir Le répertoire de sortie
     * @throws IOException En cas d'erreur lors de la génération du diagramme
     */
    private static void generateSequenceDiagram(String outputDir) throws IOException {
        System.out.println("Génération du diagramme de séquence...");

        Path plantUmlFile = Paths.get(outputDir, "sequence-build-process.puml");
        
        try (FileWriter writer = new FileWriter(plantUmlFile.toFile())) {
            writer.write("@startuml\n");
            writer.write("!theme plain\n");
            writer.write("skinparam sequenceArrowThickness 2\n");
            writer.write("title Diagramme de Séquence - Processus de Build\n\n");

            // Définir les participants
            writer.write("actor Utilisateur\n");
            writer.write("participant \"BuildController\" as Controller\n");
            writer.write("participant \"BuildService\" as Service\n");
            writer.write("participant \"BuildRepository\" as Repository\n");
            writer.write("participant \"Docker\" as Docker\n");
            writer.write("participant \"build-runner.sh\" as Script\n");
            writer.write("participant \"Maven\" as Maven\n");
            writer.write("database \"Base de données\" as DB\n\n");

            // Séquence de déclenchement d'un build
            writer.write("== Déclenchement d'un build ==\n\n");
            writer.write("Utilisateur -> Controller : triggerBuild(projectId)\n");
            writer.write("activate Controller\n");
            writer.write("Controller -> Service : triggerBuild(projectId)\n");
            writer.write("activate Service\n");
            writer.write("Service -> Repository : findById(projectId)\n");
            writer.write("activate Repository\n");
            writer.write("Repository -> DB : SELECT * FROM projects WHERE id = projectId\n");
            writer.write("DB --> Repository : Project\n");
            writer.write("Repository --> Service : Project\n");
            writer.write("deactivate Repository\n");
            writer.write("Service -> Service : new Build(project)\n");
            writer.write("Service -> Repository : save(build)\n");
            writer.write("activate Repository\n");
            writer.write("Repository -> DB : INSERT INTO builds\n");
            writer.write("DB --> Repository : Build\n");
            writer.write("Repository --> Service : savedBuild\n");
            writer.write("deactivate Repository\n");
            writer.write("Service --> Controller : savedBuild\n");
            writer.write("deactivate Service\n");
            writer.write("Controller --> Utilisateur : redirect to build details\n");
            writer.write("deactivate Controller\n\n");

            // Séquence d'exécution d'un build
            writer.write("== Exécution asynchrone du build ==\n\n");
            writer.write("Service -> Service : executeBuild(savedBuild)\n");
            writer.write("activate Service\n");
            writer.write("Service -> Repository : updateStatus(RUNNING)\n");
            writer.write("Repository -> DB : UPDATE builds SET status = 'RUNNING'\n");
            writer.write("Service -> Service : prepareRunnerScript()\n");
            writer.write("Service -> Service : createUniqueWorkspace(buildId)\n");
            writer.write("Service -> Docker : run(image, script, workspace)\n");
            writer.write("activate Docker\n");
            writer.write("Docker -> Script : execute(repoUrl, branch)\n");
            writer.write("activate Script\n");
            writer.write("Script -> Script : cleanWorkspace()\n");
            
            // Vérification du type de projet
            writer.write("alt Projet de démonstration\n");
            writer.write("  Script -> Script : createDemoProject()\n");
            writer.write("else Projet standard\n");
            writer.write("  Script -> Script : convertGitHubUrl()\n");
            writer.write("  Script -> Script : gitClone(repoUrl, branch)\n");
            writer.write("end\n");
            
            writer.write("Script -> Maven : mvn clean package\n");
            writer.write("activate Maven\n");
            writer.write("Maven --> Script : buildResult\n");
            writer.write("deactivate Maven\n");
            writer.write("Script --> Docker : exitCode\n");
            writer.write("deactivate Script\n");
            writer.write("Docker --> Service : exitCode, logs\n");
            writer.write("deactivate Docker\n");
            
            // Mise à jour du statut du build
            writer.write("alt exitCode == 0\n");
            writer.write("  Service -> Repository : updateStatus(SUCCESS)\n");
            writer.write("  Repository -> DB : UPDATE builds SET status = 'SUCCESS'\n");
            writer.write("else exitCode != 0\n");
            writer.write("  Service -> Repository : updateStatus(FAILURE)\n");
            writer.write("  Repository -> DB : UPDATE builds SET status = 'FAILURE'\n");
            writer.write("end\n");
            
            writer.write("Service -> Repository : saveLogs(logs)\n");
            writer.write("Repository -> DB : UPDATE builds SET logs = '...'\n");
            writer.write("deactivate Service\n\n");

            writer.write("@enduml\n");
        }

        System.out.println("Diagramme de séquence généré : " + plantUmlFile);
    }
}
