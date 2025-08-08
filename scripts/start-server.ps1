# Script de démarrage du serveur Shivas

Write-Host "=== Démarrage du serveur Dofus Shivas ===" -ForegroundColor Green

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
    Write-Log "Vérification des prérequis..." "INFO"
    
    # Vérifier que Java est disponible
    try {
        $null = java -version 2>&1
        Write-Log "Java disponible" "SUCCESS"
    } catch {
        Write-Log "Java non disponible" "ERROR"
        exit 1
    }
    
    # Vérifier que le projet est compilé
    if (-not (Test-Path "build")) {
        Write-Log "Projet non compilé, compilation en cours..." "WARNING"
        & ".\gradlew.bat" build
    }
    
    Write-Log "Démarrage du serveur Shivas..." "INFO"
    Write-Log "Serveur de connexion: 127.0.0.1:6789" "INFO"
    Write-Log "Serveur de jeu: 127.0.0.1:9876" "INFO"
    Write-Log "Appuyez sur Ctrl+C pour arrêter le serveur" "WARNING"
    
    # Démarrer le serveur
    & ".\gradlew.bat" :shivas-host:run
    
} catch {
    Write-Log "Erreur lors du démarrage: $_" "ERROR"
    exit 1
}
"@
</invoke>