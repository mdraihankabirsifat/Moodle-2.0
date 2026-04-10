#!/bin/bash

# Change the current working directory to the location of this script
cd "$(dirname "$0")" || exit

echo "======================================"
echo "    Starting Moodle 2.0 (Mac)         "
echo "======================================"

# Check if Java is installed
if type -p java > /dev/null; then
    echo "[INFO] Java is installed."
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]]; then
    echo "[INFO] Java found via JAVA_HOME."
else
    echo "[ERROR] Java is not installed or not found in PATH."
    echo "Please install Java (JDK) to run this application."
    echo "Press Enter to exit..."
    read -r
    exit 1
fi

# Ensure Maven Wrapper is executable
chmod +x ./mvnw

echo "[INFO] Launching the application using Maven..."
# Run the JavaFX app
./mvnw clean javafx:run

echo "Press Enter to exit..."
read -r
