package com.cicd.demo.uml;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.plantuml.SourceFileReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Générateur de diagramme de classes pour le projet Mini-CI/CD.
 * Cette classe analyse le code source Java et génère un diagramme de classes UML
 * en utilisant PlantUML.
 */
@Slf4j
public class ClassDiagramGenerator {

    private final Path sourcePath;
    private final Path outputPath;
    private final Map<String, ClassInfo> classInfoMap = new HashMap<>();

    public ClassDiagramGenerator(Path sourcePath, Path outputPath) {
        this.sourcePath = sourcePath;
        this.outputPath = outputPath;
    }

    /**
     * Génère le diagramme de classes UML.
     *
     * @return Le chemin du fichier du diagramme généré
     * @throws IOException En cas d'erreur lors de la génération du diagramme
     */
    public String generate() throws IOException {
        log.info("Génération du diagramme de classes...");

        // Analyser les fichiers Java
        analyzeJavaFiles();

        // Générer le fichier PlantUML
        String plantUmlPath = generatePlantUmlFile();

        // Générer le diagramme à partir du fichier PlantUML
        return generateDiagram(plantUmlPath);
    }

    /**
     * Analyse les fichiers Java pour extraire les informations sur les classes.
     *
     * @throws IOException En cas d'erreur lors de la lecture des fichiers
     */
    private void analyzeJavaFiles() throws IOException {
        log.info("Analyse des fichiers Java dans {}", sourcePath);

        try (Stream<Path> paths = Files.walk(sourcePath)) {
            List<Path> javaFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .collect(Collectors.toList());

            log.info("Nombre de fichiers Java trouvés : {}", javaFiles.size());

            for (Path javaFile : javaFiles) {
                try {
                    analyzeJavaFile(javaFile);
                } catch (Exception e) {
                    log.error("Erreur lors de l'analyse du fichier {}", javaFile, e);
                }
            }
        }
    }

    /**
     * Analyse un fichier Java pour extraire les informations sur les classes.
     *
     * @param javaFile Le fichier Java à analyser
     * @throws IOException En cas d'erreur lors de la lecture du fichier
     */
    private void analyzeJavaFile(Path javaFile) throws IOException {
        log.debug("Analyse du fichier {}", javaFile);

        CompilationUnit cu = new JavaParser().parse(javaFile).getResult().orElseThrow();

        // Extraire les classes et interfaces
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
            String className = classDecl.getNameAsString();
            String packageName = cu.getPackageDeclaration().map(pd -> pd.getNameAsString()).orElse("");
            String fullClassName = packageName + "." + className;

            ClassInfo classInfo = new ClassInfo();
            classInfo.setName(className);
            classInfo.setPackageName(packageName);
            classInfo.setInterface(classDecl.isInterface());

            // Extraire les champs
            classDecl.findAll(FieldDeclaration.class).forEach(field -> {
                field.getVariables().forEach(variable -> {
                    String fieldName = variable.getNameAsString();
                    String fieldType = variable.getTypeAsString();
                    String visibility = getVisibility(field);
                    classInfo.addField(fieldName, fieldType, visibility);
                });
            });

            // Extraire les méthodes
            classDecl.findAll(MethodDeclaration.class).forEach(method -> {
                String methodName = method.getNameAsString();
                String returnType = method.getTypeAsString();
                String visibility = getVisibility(method);
                classInfo.addMethod(methodName, returnType, visibility);
            });

            // Extraire les relations d'héritage
            classDecl.getExtendedTypes().forEach(extendedType -> {
                String superClassName = extendedType.getNameAsString();
                classInfo.setSuperClass(superClassName);
            });

            // Extraire les relations d'implémentation
            classDecl.getImplementedTypes().forEach(implementedType -> {
                String interfaceName = implementedType.getNameAsString();
                classInfo.addImplementedInterface(interfaceName);
            });

            // Extraire les relations d'association
            classDecl.findAll(FieldDeclaration.class).forEach(field -> {
                field.getVariables().forEach(variable -> {
                    String fieldType = variable.getTypeAsString();
                    if (!isPrimitiveType(fieldType)) {
                        classInfo.addAssociation(fieldType);
                    }
                });
            });

            classInfoMap.put(fullClassName, classInfo);
        });
    }

    /**
     * Génère le fichier PlantUML à partir des informations sur les classes.
     *
     * @return Le chemin du fichier PlantUML généré
     * @throws IOException En cas d'erreur lors de l'écriture du fichier
     */
    private String generatePlantUmlFile() throws IOException {
        log.info("Génération du fichier PlantUML pour le diagramme de classes");

        Path plantUmlFile = outputPath.resolve("class-diagram.puml");
        
        try (FileWriter writer = new FileWriter(plantUmlFile.toFile())) {
            writer.write("@startuml\n");
            writer.write("!theme plain\n");
            writer.write("skinparam classAttributeIconSize 0\n");
            writer.write("skinparam classFontStyle bold\n");
            writer.write("skinparam classBackgroundColor #FEFECE\n");
            writer.write("skinparam classBorderColor #A80036\n");
            writer.write("skinparam packageBackgroundColor #FEFECE\n");
            writer.write("skinparam packageBorderColor #A80036\n");
            writer.write("skinparam arrowColor #A80036\n");
            writer.write("skinparam stereotypeCBackgroundColor #ADD1B2\n");
            writer.write("skinparam stereotypeABackgroundColor #A9DCDF\n");
            
            writer.write("title Diagramme de Classes - Mini-CI/CD\n\n");

            // Définir les packages
            Map<String, List<ClassInfo>> packageMap = new HashMap<>();
            for (ClassInfo classInfo : classInfoMap.values()) {
                packageMap.computeIfAbsent(classInfo.getPackageName(), k -> new ArrayList<>()).add(classInfo);
            }

            // Générer les packages et les classes
            for (Map.Entry<String, List<ClassInfo>> entry : packageMap.entrySet()) {
                String packageName = entry.getKey();
                List<ClassInfo> classes = entry.getValue();

                writer.write("package " + packageName + " {\n");

                // Générer les classes
                for (ClassInfo classInfo : classes) {
                    if (classInfo.isInterface()) {
                        writer.write("  interface " + classInfo.getName() + " {\n");
                    } else {
                        writer.write("  class " + classInfo.getName() + " {\n");
                    }

                    // Générer les champs
                    for (FieldInfo fieldInfo : classInfo.getFields()) {
                        writer.write("    " + fieldInfo.getVisibility() + " " + fieldInfo.getName() + " : " + fieldInfo.getType() + "\n");
                    }

                    // Générer les méthodes
                    for (MethodInfo methodInfo : classInfo.getMethods()) {
                        writer.write("    " + methodInfo.getVisibility() + " " + methodInfo.getName() + "() : " + methodInfo.getReturnType() + "\n");
                    }

                    writer.write("  }\n\n");
                }

                writer.write("}\n\n");
            }

            // Générer les relations
            for (ClassInfo classInfo : classInfoMap.values()) {
                String className = classInfo.getPackageName() + "." + classInfo.getName();

                // Relations d'héritage
                if (classInfo.getSuperClass() != null) {
                    writer.write(classInfo.getName() + " --|> " + classInfo.getSuperClass() + "\n");
                }

                // Relations d'implémentation
                for (String interfaceName : classInfo.getImplementedInterfaces()) {
                    writer.write(classInfo.getName() + " ..|> " + interfaceName + "\n");
                }

                // Relations d'association
                for (String associatedClass : classInfo.getAssociations()) {
                    writer.write(classInfo.getName() + " --> " + associatedClass + "\n");
                }
            }

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

    /**
     * Détermine la visibilité d'un élément (champ ou méthode).
     *
     * @param element L'élément dont on veut déterminer la visibilité
     * @return La visibilité de l'élément
     */
    private String getVisibility(com.github.javaparser.ast.nodeTypes.NodeWithModifiers<?> element) {
        if (element.getModifiers().contains(com.github.javaparser.ast.Modifier.PUBLIC)) {
            return "+";
        } else if (element.getModifiers().contains(com.github.javaparser.ast.Modifier.PROTECTED)) {
            return "#";
        } else if (element.getModifiers().contains(com.github.javaparser.ast.Modifier.PRIVATE)) {
            return "-";
        } else {
            return "~"; // package-private
        }
    }

    /**
     * Vérifie si un type est un type primitif.
     *
     * @param type Le type à vérifier
     * @return true si le type est un type primitif, false sinon
     */
    private boolean isPrimitiveType(String type) {
        return type.equals("int") || type.equals("long") || type.equals("double") || type.equals("float")
                || type.equals("boolean") || type.equals("char") || type.equals("byte") || type.equals("short")
                || type.equals("void") || type.equals("String");
    }

    /**
     * Classe interne représentant les informations sur une classe.
     */
    private static class ClassInfo {
        private String name;
        private String packageName;
        private boolean isInterface;
        private String superClass;
        private final List<String> implementedInterfaces = new ArrayList<>();
        private final List<FieldInfo> fields = new ArrayList<>();
        private final List<MethodInfo> methods = new ArrayList<>();
        private final List<String> associations = new ArrayList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public boolean isInterface() {
            return isInterface;
        }

        public void setInterface(boolean isInterface) {
            this.isInterface = isInterface;
        }

        public String getSuperClass() {
            return superClass;
        }

        public void setSuperClass(String superClass) {
            this.superClass = superClass;
        }

        public List<String> getImplementedInterfaces() {
            return implementedInterfaces;
        }

        public void addImplementedInterface(String interfaceName) {
            implementedInterfaces.add(interfaceName);
        }

        public List<FieldInfo> getFields() {
            return fields;
        }

        public void addField(String name, String type, String visibility) {
            fields.add(new FieldInfo(name, type, visibility));
        }

        public List<MethodInfo> getMethods() {
            return methods;
        }

        public void addMethod(String name, String returnType, String visibility) {
            methods.add(new MethodInfo(name, returnType, visibility));
        }

        public List<String> getAssociations() {
            return associations;
        }

        public void addAssociation(String associatedClass) {
            associations.add(associatedClass);
        }
    }

    /**
     * Classe interne représentant les informations sur un champ.
     */
    private static class FieldInfo {
        private final String name;
        private final String type;
        private final String visibility;

        public FieldInfo(String name, String type, String visibility) {
            this.name = name;
            this.type = type;
            this.visibility = visibility;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getVisibility() {
            return visibility;
        }
    }

    /**
     * Classe interne représentant les informations sur une méthode.
     */
    private static class MethodInfo {
        private final String name;
        private final String returnType;
        private final String visibility;

        public MethodInfo(String name, String returnType, String visibility) {
            this.name = name;
            this.returnType = returnType;
            this.visibility = visibility;
        }

        public String getName() {
            return name;
        }

        public String getReturnType() {
            return returnType;
        }

        public String getVisibility() {
            return visibility;
        }
    }
}
