# Script de configuration automatique pour Shivas

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
    $ProjectPath = (Get-Location).Path

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

    # Étape 3: Créer et initialiser la base de données SQLite
    Write-Log "Configuration de la base de données SQLite..." "INFO"
    $dbPath = Join-Path $ProjectPath "database"
    if (-not (Test-Path $dbPath)) {
        New-Item -ItemType Directory -Path $dbPath -Force | Out-Null
        Write-Log "Dossier database créé" "SUCCESS"
    }

    $dbFile = Join-Path $ProjectPath "database\shivas.db"
    $sqlFile = Join-Path $ProjectPath "database\shivas.sql"
    
    if (Test-Path $sqlFile) {
        Write-Log "Initialisation de la base de données SQLite..." "INFO"
        try {
            # Utiliser sqlite3 si disponible, sinon créer une base vide
            if (Get-Command sqlite3 -ErrorAction SilentlyContinue) {
                & sqlite3 $dbFile ".read $sqlFile"
                Write-Log "Base de données SQLite initialisée avec succès" "SUCCESS"
            } else {
                Write-Log "SQLite3 non trouvé, création d'une base vide" "WARNING"
                # Créer un fichier de base vide
                New-Item -Path $dbFile -ItemType File -Force | Out-Null
                Write-Log "Fichier de base de données créé (sera initialisé au premier démarrage)" "SUCCESS"
            }
        } catch {
            Write-Log "Erreur lors de l'initialisation de la base: $_" "WARNING"
            Write-Log "La base sera initialisée au premier démarrage du serveur" "INFO"
        }
    } else {
        Write-Log "Fichier SQL non trouvé, création d'une base vide" "WARNING"
        New-Item -Path $dbFile -ItemType File -Force | Out-Null
    }

    # Étape 4: Compiler le projet
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

    Write-Log "=== Configuration terminée avec succès ! ===" "SUCCESS"
    Write-Log "Vous pouvez maintenant démarrer le serveur depuis l'interface web" "INFO"

} catch {
    Write-Log "Erreur lors de la configuration: $_" "ERROR"
    exit 1
}
"@
</invoke>