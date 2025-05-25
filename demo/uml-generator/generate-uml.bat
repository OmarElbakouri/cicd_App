@echo off
echo ======================================================
echo Générateur de diagrammes UML pour Mini-CI/CD
echo ======================================================
echo.

echo Compilation du générateur UML...
call mvn clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo Erreur lors de la compilation du générateur UML.
    exit /b %ERRORLEVEL%
)

echo.
echo Exécution du générateur UML...
echo.

mkdir -p diagrams

java -jar target\uml-generator-1.0-SNAPSHOT-jar-with-dependencies.jar ..\src\main\java\com\cicd\demo diagrams

echo.
echo ======================================================
echo Génération des diagrammes UML terminée !
echo Les diagrammes sont disponibles dans le répertoire "diagrams".
echo ======================================================
