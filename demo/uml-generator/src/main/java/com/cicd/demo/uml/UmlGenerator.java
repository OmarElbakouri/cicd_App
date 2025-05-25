package com.cicd.demo.uml;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Générateur de diagrammes UML pour le projet Mini-CI/CD.
 * Cette classe permet de générer différents types de diagrammes UML :
 * - Diagramme de classes
 * - Diagramme de cas d'utilisation
 * - Diagrammes de séquence (modélisation dynamique)
 */
@Slf4j
public class UmlGenerator {

    private static final String DEFAULT_SOURCE_DIR = "../src/main/java/com/cicd/demo";
    private static final String DEFAULT_OUTPUT_DIR = "diagrams";

    public static void main(String[] args) {
        log.info("Démarrage du générateur UML pour Mini-CI/CD");

        String sourceDir = DEFAULT_SOURCE_DIR;
        String outputDir = DEFAULT_OUTPUT_DIR;

        // Traitement des arguments
        if (args.length >= 1) {
            sourceDir = args[0];
        }
        if (args.length >= 2) {
            outputDir = args[1];
        }

        // Création du répertoire de sortie s'il n'existe pas
        File outputDirFile = new File(outputDir);
        if (!outputDirFile.exists()) {
            if (outputDirFile.mkdirs()) {
                log.info("Répertoire de sortie créé : {}", outputDir);
            } else {
                log.error("Impossible de créer le répertoire de sortie : {}", outputDir);
                return;
            }
        }

        Path sourcePath = Paths.get(sourceDir).toAbsolutePath();
        Path outputPath = Paths.get(outputDir).toAbsolutePath();

        log.info("Répertoire source : {}", sourcePath);
        log.info("Répertoire de sortie : {}", outputPath);

        try {
            // Génération du diagramme de classes
            ClassDiagramGenerator classDiagramGenerator = new ClassDiagramGenerator(sourcePath, outputPath);
            String classDiagramPath = classDiagramGenerator.generate();
            log.info("Diagramme de classes généré : {}", classDiagramPath);

            // Génération du diagramme de cas d'utilisation
            UseCaseDiagramGenerator useCaseDiagramGenerator = new UseCaseDiagramGenerator(outputPath);
            String useCaseDiagramPath = useCaseDiagramGenerator.generate();
            log.info("Diagramme de cas d'utilisation généré : {}", useCaseDiagramPath);

            // Génération des diagrammes de séquence
            SequenceDiagramGenerator sequenceDiagramGenerator = new SequenceDiagramGenerator(sourcePath, outputPath);
            String[] sequenceDiagramPaths = sequenceDiagramGenerator.generate();
            log.info("Diagrammes de séquence générés : {}", Arrays.toString(sequenceDiagramPaths));

            log.info("Génération des diagrammes UML terminée avec succès");
            log.info("Les diagrammes sont disponibles dans le répertoire : {}", outputPath);
        } catch (Exception e) {
            log.error("Erreur lors de la génération des diagrammes UML", e);
        }
    }
}
