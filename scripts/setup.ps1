# Script de configuration automatique pour Shivas
param(
    [string]$ProjectPath = "c:\Users\Montoya\Inkan"
)

Write-Host "=== Configuration automatique du serveur Dofus Shivas ===" -ForegroundColor Green

# Fonction pour écrire des logs
function Write-Log {
    param([string]$Message, [string]$Type = "INFO")
    $timestamp = Get-Date -Format "HH:mm:ss"
    $color = switch($Type) {
        "ERROR" { "Red" }
        "SUCCESS" { "Green" }
        "WARNING" { "Yellow" }
        default { "White" }
    }
    Write-Host "[$timestamp] $Message" -ForegroundColor $color
}

try {
    Set-Location $ProjectPath
    
    # Étape 1: Vérifier Java
    Write-Log "Vérification de Java..." "INFO"
    try {
        $javaVersion = java -version 2>&1 | Select-String "version"
        if ($javaVersion) {
            Write-Log "Java détecté: $javaVersion" "SUCCESS"
        } else {
            throw "Java non trouvé"
        }
    } catch {
        Write-Log "Java n'est pas installé ou n'est pas dans le PATH" "ERROR"
        Write-Log "Veuillez installer Java 8+ avant de continuer" "ERROR"
        exit 1
    }

    # Étape 2: Vérifier Gradle
    Write-Log "Vérification de Gradle..." "INFO"
    if (Test-Path "gradlew.bat") {
        Write-Log "Gradle Wrapper trouvé" "SUCCESS"
    } else {
        Write-Log "Gradle Wrapper non trouvé" "ERROR"
        exit 1
    }

    # Étape 3: Configurer le fichier config.yaml
    Write-Log "Configuration du fichier config.yaml..." "INFO"
    $configPath = Join-Path $ProjectPath "config.yaml"
    if (Test-Path $configPath) {
        # Mettre à jour les chemins dans config.yaml
        $config = Get-Content $configPath -Raw
        $config = $config -replace '/Users/antoine/Workspace/Shivas/data/', "$ProjectPath\data\"
        $config = $config -replace '/Users/antoine/Workspace/Shivas/mods/', "$ProjectPath\mods\"
        Set-Content $configPath $config -Encoding UTF8
        Write-Log "Configuration mise à jour avec les chemins corrects" "SUCCESS"
    } else {
        Write-Log "Fichier config.yaml non trouvé" "ERROR"
        exit 1
    }

    # Étape 4: Créer la base de données SQLite (alternative à MariaDB)
    Write-Log "Configuration de la base de données..." "INFO"
    $dbPath = Join-Path $ProjectPath "database"
    if (-not (Test-Path $dbPath)) {
        New-Item -ItemType Directory -Path $dbPath -Force | Out-Null
        Write-Log "Dossier database créé" "SUCCESS"
    }

    # Créer un fichier SQL de base
    $sqlContent = @"
-- Base de données Shivas
CREATE TABLE IF NOT EXISTS accounts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(255),
    secret_question VARCHAR(255),
    secret_answer VARCHAR(255),
    rights INTEGER DEFAULT 0,
    banned BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS players (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_id INTEGER NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    breed INTEGER NOT NULL,
    gender BOOLEAN NOT NULL,
    colors VARCHAR(255),
    level INTEGER DEFAULT 200,
    experience BIGINT DEFAULT 0,
    kamas BIGINT DEFAULT 1000000,
    map_id INTEGER DEFAULT 7411,
    cell_id INTEGER DEFAULT 255,
    direction INTEGER DEFAULT 1,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- Insérer un compte de test
INSERT OR IGNORE INTO accounts (name, password, nickname, rights) 
VALUES ('admin', 'admin', 'Administrateur', 1);

INSERT OR IGNORE INTO accounts (name, password, nickname, rights) 
VALUES ('test', 'test', 'Joueur Test', 0);
"@

    $sqlFile = Join-Path $ProjectPath "database\shivas.sql"
    Set-Content $sqlFile $sqlContent -Encoding UTF8
    Write-Log "Base de données SQL créée" "SUCCESS"

    # Étape 5: Compiler le projet
    Write-Log "Compilation du projet..." "INFO"
    try {
        $buildResult = & ".\gradlew.bat" clean build 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Log "Compilation réussie" "SUCCESS"
        } else {
            Write-Log "Erreur lors de la compilation" "ERROR"
            Write-Log $buildResult "ERROR"
        }
    } catch {
        Write-Log "Erreur lors de l'exécution de Gradle: $_" "ERROR"
    }

    # Étape 6: Créer les scripts de démarrage
    Write-Log "Création des scripts de démarrage..." "INFO"
    
    $startScript = @"
@echo off
echo Demarrage du serveur Dofus Shivas...
cd /d "$ProjectPath"
gradlew.bat :shivas-host:run
pause
"@

    $startScriptPath = Join-Path $ProjectPath "scripts\start-server.bat"
    $scriptsDir = Join-Path $ProjectPath "scripts"
    if (-not (Test-Path $scriptsDir)) {
        New-Item -ItemType Directory -Path $scriptsDir -Force | Out-Null
    }
    Set-Content $startScriptPath $startScript -Encoding ASCII
    Write-Log "Script de démarrage créé: $startScriptPath" "SUCCESS"

    Write-Log "=== Configuration terminée avec succès ! ===" "SUCCESS"
    Write-Log "Vous pouvez maintenant démarrer le serveur depuis l'interface web" "INFO"

} catch {
    Write-Log "Erreur lors de la configuration: $_" "ERROR"
    exit 1
}
"@
</invoke>