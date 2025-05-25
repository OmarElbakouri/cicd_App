package com.cicd.demo.uml;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.plantuml.SourceFileReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Générateur de diagrammes de séquence pour le projet Mini-CI/CD.
 * Cette classe génère des diagrammes de séquence UML en utilisant PlantUML
 * pour illustrer les interactions dynamiques entre les composants du système.
 */
@Slf4j
public class SequenceDiagramGenerator {

    private final Path sourcePath;
    private final Path outputPath;

    public SequenceDiagramGenerator(Path sourcePath, Path outputPath) {
        this.sourcePath = sourcePath;
        this.outputPath = outputPath;
    }

    /**
     * Génère les diagrammes de séquence UML.
     *
     * @return Les chemins des fichiers des diagrammes générés
     * @throws IOException En cas d'erreur lors de la génération des diagrammes
     */
    public String[] generate() throws IOException {
        log.info("Génération des diagrammes de séquence...");

        List<String> diagramPaths = new ArrayList<>();

        // Générer le diagramme de séquence pour le processus de build
        String buildProcessDiagramPath = generateBuildProcessDiagram();
        diagramPaths.add(buildProcessDiagramPath);

        // Générer le diagramme de séquence pour la création de projet
        String createProjectDiagramPath = generateCreateProjectDiagram();
        diagramPaths.add(createProjectDiagramPath);

        return diagramPaths.toArray(new String[0]);
    }

    /**
     * Génère le diagramme de séquence pour le processus de build.
     *
     * @return Le chemin du fichier du diagramme généré
     * @throws IOException En cas d'erreur lors de la génération du diagramme
     */
    private String generateBuildProcessDiagram() throws IOException {
        log.info("Génération du diagramme de séquence pour le processus de build");

        Path plantUmlFile = outputPath.resolve("sequence-build-process.puml");
        
        try (FileWriter writer = new FileWriter(plantUmlFile.toFile())) {
            writer.write("@startuml\n");
            writer.write("!theme plain\n");
            writer.write("skinparam sequenceArrowThickness 2\n");
            writer.write("skinparam sequenceParticipantBorderColor #A80036\n");
            writer.write("skinparam sequenceParticipantBackgroundColor #FEFECE\n");
            writer.write("skinparam sequenceLifeLineBorderColor #A80036\n");
            writer.write("skinparam sequenceLifeLineBackgroundColor #FEFECE\n");
            writer.write("skinparam sequenceActorBorderColor #A80036\n");
            writer.write("skinparam sequenceActorBackgroundColor #FEFECE\n");
            writer.write("skinparam sequenceBoxBorderColor #A80036\n");
            writer.write("skinparam sequenceBoxBackgroundColor #FEFECE\n");
            
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

            // Consultation des résultats
            writer.write("== Consultation des résultats ==\n\n");
            writer.write("Utilisateur -> Controller : viewBuild(buildId)\n");
            writer.write("activate Controller\n");
            writer.write("Controller -> Service : getBuildById(buildId)\n");
            writer.write("activate Service\n");
            writer.write("Service -> Repository : findById(buildId)\n");
            writer.write("Repository -> DB : SELECT * FROM builds WHERE id = buildId\n");
            writer.write("DB --> Repository : Build\n");
            writer.write("Repository --> Service : Build\n");
            writer.write("Service --> Controller : Build\n");
            writer.write("deactivate Service\n");
            writer.write("Controller --> Utilisateur : build details page\n");
            writer.write("deactivate Controller\n");
            
            writer.write("@enduml\n");
        }

        log.info("Fichier PlantUML généré : {}", plantUmlFile);
        return generateDiagram(plantUmlFile.toString());
    }

    /**
     * Génère le diagramme de séquence pour la création de projet.
     *
     * @return Le chemin du fichier du diagramme généré
     * @throws IOException En cas d'erreur lors de la génération du diagramme
     */
    private String generateCreateProjectDiagram() throws IOException {
        log.info("Génération du diagramme de séquence pour la création de projet");

        Path plantUmlFile = outputPath.resolve("sequence-create-project.puml");
        
        try (FileWriter writer = new FileWriter(plantUmlFile.toFile())) {
            writer.write("@startuml\n");
            writer.write("!theme plain\n");
            writer.write("skinparam sequenceArrowThickness 2\n");
            writer.write("skinparam sequenceParticipantBorderColor #A80036\n");
            writer.write("skinparam sequenceParticipantBackgroundColor #FEFECE\n");
            writer.write("skinparam sequenceLifeLineBorderColor #A80036\n");
            writer.write("skinparam sequenceLifeLineBackgroundColor #FEFECE\n");
            writer.write("skinparam sequenceActorBorderColor #A80036\n");
            writer.write("skinparam sequenceActorBackgroundColor #FEFECE\n");
            writer.write("skinparam sequenceBoxBorderColor #A80036\n");
            writer.write("skinparam sequenceBoxBackgroundColor #FEFECE\n");
            
            writer.write("title Diagramme de Séquence - Création de Projet\n\n");

            // Définir les participants
            writer.write("actor Utilisateur\n");
            writer.write("participant \"ProjectController\" as Controller\n");
            writer.write("participant \"ProjectService\" as Service\n");
            writer.write("participant \"ProjectRepository\" as Repository\n");
            writer.write("database \"Base de données\" as DB\n\n");

            // Séquence de création d'un projet
            writer.write("== Création d'un projet ==\n\n");
            writer.write("Utilisateur -> Controller : showCreateForm()\n");
            writer.write("activate Controller\n");
            writer.write("Controller --> Utilisateur : formulaire de création\n");
            writer.write("deactivate Controller\n");
            
            writer.write("Utilisateur -> Controller : createProject(projectData)\n");
            writer.write("activate Controller\n");
            
            // Validation des données
            writer.write("Controller -> Controller : validate(projectData)\n");
            writer.write("alt Validation réussie\n");
            writer.write("  Controller -> Service : createProject(projectData)\n");
            writer.write("  activate Service\n");
            writer.write("  Service -> Service : new Project(projectData)\n");
            writer.write("  Service -> Repository : save(project)\n");
            writer.write("  activate Repository\n");
            writer.write("  Repository -> DB : INSERT INTO projects\n");
            writer.write("  DB --> Repository : Project\n");
            writer.write("  Repository --> Service : savedProject\n");
            writer.write("  deactivate Repository\n");
            writer.write("  Service --> Controller : savedProject\n");
            writer.write("  deactivate Service\n");
            writer.write("  Controller --> Utilisateur : redirect to project details\n");
            writer.write("else Validation échouée\n");
            writer.write("  Controller --> Utilisateur : formulaire avec erreurs\n");
            writer.write("end\n");
            
            writer.write("deactivate Controller\n");
            
            writer.write("@enduml\n");
        }

        log.info("Fichier PlantUML généré : {}", plantUmlFile);
        return generateDiagram(plantUmlFile.toString());
    }

    /**
     * Génère le diagramme à partir du fichier PlantUML.
     *
     * @param plantUmlPath Le chemin du fichier PlantUML
     * @return Le chemin du fichier du diagramme généré
     * @throws IOException En cas d'erreur lors de la génération du diagramme
     */
    private String generateDiagram(String plantUmlPath) throws IOException {
        log.info("Génération du diagramme à partir du fichier PlantUML : {}", plantUmlPath);

        File plantUmlFile = new File(plantUmlPath);
        SourceFileReader reader = new SourceFileReader(plantUmlFile);
        List<?> generatedImages = reader.getGeneratedImages();

        if (generatedImages.isEmpty()) {
            throw new IOException("Aucun diagramme n'a été généré");
        }

        // Récupérer le premier fichier généré
        Object firstImage = generatedImages.get(0);
        String diagramPath = firstImage.toString();
        log.info("Diagramme généré : {}", diagramPath);
        return diagramPath;
    }
}
