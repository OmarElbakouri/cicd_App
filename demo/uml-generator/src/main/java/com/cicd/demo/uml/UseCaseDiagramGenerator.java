package com.cicd.demo.uml;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.plantuml.SourceFileReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Générateur de diagramme de cas d'utilisation pour le projet Mini-CI/CD.
 * Cette classe génère un diagramme de cas d'utilisation UML en utilisant PlantUML.
 */
@Slf4j
public class UseCaseDiagramGenerator {

    private final Path outputPath;

    public UseCaseDiagramGenerator(Path outputPath) {
        this.outputPath = outputPath;
    }

    /**
     * Génère le diagramme de cas d'utilisation UML.
     *
     * @return Le chemin du fichier du diagramme généré
     * @throws IOException En cas d'erreur lors de la génération du diagramme
     */
    public String generate() throws IOException {
        log.info("Génération du diagramme de cas d'utilisation...");

        // Générer le fichier PlantUML
        String plantUmlPath = generatePlantUmlFile();

        // Générer le diagramme à partir du fichier PlantUML
        return generateDiagram(plantUmlPath);
    }

    /**
     * Génère le fichier PlantUML pour le diagramme de cas d'utilisation.
     *
     * @return Le chemin du fichier PlantUML généré
     * @throws IOException En cas d'erreur lors de l'écriture du fichier
     */
    private String generatePlantUmlFile() throws IOException {
        log.info("Génération du fichier PlantUML pour le diagramme de cas d'utilisation");

        Path plantUmlFile = outputPath.resolve("use-case-diagram.puml");
        
        try (FileWriter writer = new FileWriter(plantUmlFile.toFile())) {
            writer.write("@startuml\n");
            writer.write("!theme plain\n");
            writer.write("skinparam actorStyle awesome\n");
            writer.write("skinparam usecaseBackgroundColor #FEFECE\n");
            writer.write("skinparam usecaseBorderColor #A80036\n");
            writer.write("skinparam actorBackgroundColor #FEFECE\n");
            writer.write("skinparam actorBorderColor #A80036\n");
            writer.write("skinparam arrowColor #A80036\n");
            
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

        log.info("Fichier PlantUML généré : {}", plantUmlFile);
        return plantUmlFile.toString();
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
