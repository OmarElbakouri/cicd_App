#!/bin/sh

# Build runner script for Mini-CI/CD
# This script is executed inside a Docker container to clone and build a project

set -e

if [ "$#" -lt 2 ]; then
    echo "Usage: $0 <repository_url> <branch>"
    exit 1
fi

REPO_URL=$1
BRANCH=$2
WORKSPACE="/workspace"

echo "Starting build process..."
echo "Repository: $REPO_URL"
echo "Branch: $BRANCH"
echo "Workspace: $WORKSPACE"

# Clean workspace
echo "Cleaning workspace..."
# Force remove all contents including hidden files
rm -rf $WORKSPACE/* $WORKSPACE/.[!.]* $WORKSPACE/..?* 2>/dev/null || true
# Make sure the directory exists and is empty
mkdir -p $WORKSPACE
cd $WORKSPACE
ls -la

# Check if we should create a demo project instead of cloning
if echo "$REPO_URL" | grep -q "demo-project"; then
    echo "Creating a demo Maven project instead of cloning..."
    cd $WORKSPACE
    
    # Create a simple Maven project structure
    mkdir -p src/main/java/com/example
    mkdir -p src/test/java/com/example
    
    # Create a simple Java class
    cat > src/main/java/com/example/App.java << 'EOF'
package com.example;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
    
    public String getGreeting() {
        return "Hello World!";
    }
}
EOF
    
    # Create a simple test class
    cat > src/test/java/com/example/AppTest.java << 'EOF'
package com.example;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
    @Test
    public void testApp() {
        App app = new App();
        assertEquals("Hello World!", app.getGreeting());
    }
}
EOF
    
    # Create a pom.xml file
    cat > pom.xml << 'EOF'
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.example</groupId>
  <artifactId>demo-project</artifactId>
  <packaging>jar</packaging>
  <version>1.0-SNAPSHOT</version>
  <name>demo-project</name>
  <url>http://maven.apache.org</url>
  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.13.2</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
  <properties>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
  </properties>
</project>
EOF
    
    echo "Demo project created successfully."
    
# If not a demo project, try to clone the repository
else
    echo "Cloning repository..."
    
    # Convert HTTPS URL to Git URL if needed
    GIT_URL=$REPO_URL
    case "$REPO_URL" in
      https://github.com/*)
        REPO_PATH=$(echo $REPO_URL | sed 's|https://github.com/||')
        GIT_URL="git://github.com/$REPO_PATH"
        echo "Converting to Git URL: $GIT_URL"
        ;;
    esac
    
    # Clone with Git protocol
    git clone -b $BRANCH $GIT_URL $WORKSPACE
    cd $WORKSPACE
fi

# Build project with Maven
echo "Building project with Maven..."
mvn clean package

# Check build status
if [ $? -eq 0 ]; then
    echo "Build successful!"
    exit 0
else
    echo "Build failed!"
    exit 1
fi
