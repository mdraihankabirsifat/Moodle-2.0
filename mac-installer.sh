#!/bin/bash

# Configuration
REPO_URL="https://github.com/mdraihankabirsifat/Moodle-2.0.git"
BRANCH="main"
INSTALL_DIR="$HOME/.moodle-app-v2"

echo "======================================"
echo "   Moodle 2.0 Updater & Launcher      "
echo "======================================"

# Check for Git
if ! type -p git > /dev/null; then
    echo "[ERROR] Git is not installed. Please install Git."
    exit 1
fi

# Check for Java
if ! type -p java > /dev/null; then
    if [[ -z "$JAVA_HOME" ]] || [[ ! -x "$JAVA_HOME/bin/java" ]]; then
        echo "[ERROR] Java is not installed or not in PATH."
        echo "Please install Java (JDK) to run this application."
        exit 1
    fi
fi

# Clone or Update the repository
if [ ! -d "$INSTALL_DIR" ]; then
    echo "[INFO] App not found locally. Downloading the latest version from GitHub..."
    git clone --branch "$BRANCH" "$REPO_URL" "$INSTALL_DIR"
else
    echo "[INFO] Updating to the latest version..."
    cd "$INSTALL_DIR" || exit
    git fetch --all
    git reset --hard "origin/$BRANCH"
fi

cd "$INSTALL_DIR" || exit

# Run the app
echo "[INFO] Launching Moodle 2.0..."
chmod +x ./mvnw
./mvnw clean javafx:run

echo "Session ended. You can close this window."
