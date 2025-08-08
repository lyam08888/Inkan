@echo off
chcp 65001 >nul
title 🎮 Serveur Dofus Shivas - Jeu Hors Ligne
color 0B

cls
echo.
echo  ╔══════════════════════════════════════════════════════════════════════════╗
echo  ║                                                                          ║
echo  ║    🎮 SERVEUR DOFUS SHIVAS - MODE HORS LIGNE 🎮                          ║
echo  ║                                                                          ║
echo  ║    ⚔️  Jouez à Dofus sans connexion internet !                          ║
echo  ║    🏠 Serveur privé local avec interface web                            ║
echo  ║    🚀 Configuration et démarrage automatiques                           ║
echo  ║                                                                          ║
echo  ║    📋 Caractéristiques du serveur:                                      ║
echo  ║    • Niveau de départ: 200                                              ║
echo  ║    • Kamas de départ: 1,000,000                                         ║
echo  ║    • Tous les waypoints activés                                         ║
echo  ║    • Points d'action: 6 / Points de mouvement: 3                       ║
echo  ║                                                                          ║
echo  ╚══════════════════════════════════════════════════════════════════════════╝
echo.

cd /d "%~dp0"

echo 🔍 Vérification des prérequis...
echo.

REM Vérifier Java
echo [1/2] Vérification de Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java n'est pas installé !
    echo.
    echo 📥 Téléchargement et installation automatique de Java...
    echo.

    powershell -Command "& {
        Write-Host '📥 Téléchargement de Java 17...' -ForegroundColor Cyan;
        $url = 'https://download.oracle.com/java/17/latest/jdk-17_windows-x64_bin.msi';
        $output = '$env:TEMP\java17.msi';
        try {
            Write-Host 'Téléchargement en cours...' -ForegroundColor Yellow;
            Invoke-WebRequest -Uri $url -OutFile $output -UseBasicParsing;
            Write-Host '🔧 Installation de Java...' -ForegroundColor Yellow;
            Start-Process msiexec.exe -Wait -ArgumentList '/i', $output, '/quiet', '/norestart';
            Write-Host '✅ Java installé avec succès !' -ForegroundColor Green;
        } catch {
            Write-Host '❌ Erreur lors de l''installation automatique' -ForegroundColor Red;
            Write-Host '📋 Veuillez installer Java manuellement:' -ForegroundColor Yellow;
            Write-Host '   https://www.oracle.com/java/technologies/downloads/' -ForegroundColor White;
        }
    }"

    echo.
    echo 🔄 Redémarrage nécessaire pour finaliser l'installation de Java
    echo 📋 Après le redémarrage, relancez ce fichier
    pause
    exit /b 1
) else (
    echo ✅ Java détecté avec succès
)

REM Vérifier Node.js
echo [2/2] Vérification de Node.js...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Node.js n'est pas installé !
    echo.
    echo 📥 Téléchargement et installation automatique de Node.js...
    echo.

    powershell -Command "& {
        Write-Host '📥 Téléchargement de Node.js 18...' -ForegroundColor Cyan;
        $url = 'https://nodejs.org/dist/v18.17.0/node-v18.17.0-x64.msi';
        $output = '$env:TEMP\nodejs.msi';
        try {
            Write-Host 'Téléchargement en cours...' -ForegroundColor Yellow;
            Invoke-WebRequest -Uri $url -OutFile $output -UseBasicParsing;
            Write-Host '🔧 Installation de Node.js...' -ForegroundColor Yellow;
            Start-Process msiexec.exe -Wait -ArgumentList '/i', $output, '/quiet', '/norestart';
            Write-Host '✅ Node.js installé avec succès !' -ForegroundColor Green;
        } catch {
            Write-Host '❌ Erreur lors de l''installation automatique' -ForegroundColor Red;
            Write-Host '📋 Veuillez installer Node.js manuellement:' -ForegroundColor Yellow;
            Write-Host '   https://nodejs.org/' -ForegroundColor White;
        }
    }"

    REM Actualiser les variables d'environnement
    echo 🔄 Actualisation des variables d'environnement...
    for /f "tokens=2*" %%a in ('reg query "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v PATH 2^>nul') do set "SysPath=%%b"
    for /f "tokens=2*" %%a in ('reg query "HKCU\Environment" /v PATH 2^>nul') do set "UserPath=%%b"
    set "PATH=%SysPath%;%UserPath%"

    REM Vérifier à nouveau
    node --version >nul 2>&1
    if %errorlevel% neq 0 (
        echo ❌ Installation automatique échouée
        echo.
        echo 📋 Veuillez installer Node.js manuellement depuis: https://nodejs.org/
        echo 🔄 Puis relancez ce fichier
        pause
        exit /b 1
    )
) else (
    echo ✅ Node.js détecté avec succès
)

echo.
echo 🛠️  Préparation de l'environnement...

REM Créer les dossiers nécessaires
if not exist "web-server" mkdir web-server
if not exist "scripts" mkdir scripts
if not exist "data" mkdir data
if not exist "mods" mkdir mods
if not exist "database" mkdir database

REM Aller dans le dossier web-server
cd web-server

REM Installer les dépendances npm
if not exist "node_modules" (
    echo 📦 Installation des dépendances...
    npm install express cors
    if %errorlevel% neq 0 (
        echo ❌ Erreur lors de l'installation des dépendances
        pause
        exit /b 1
    )
    echo ✅ Dépendances installées
) else (
    echo ✅ Dépendances déjà installées
)

echo.
echo 🚀 Démarrage de l'interface web...
echo.
echo  ╔══════════════════════════════════════════════════════════════════════════╗
echo  ║                                                                          ║
echo  ║  🌐 INTERFACE WEB DÉMARRÉE !                                            ║
echo  ║                                                                          ║
echo  ║  📍 URL: http://localhost:3000                                          ║
echo  ║                                                                          ║
echo  ║  📋 ÉTAPES SUIVANTES:                                                   ║
echo  ║                                                                          ║
echo  ║  1️⃣  Votre navigateur va s'ouvrir automatiquement                      ║
echo  ║  2️⃣  Cliquez sur "Configuration automatique"                           ║
echo  ║  3️⃣  Attendez la fin de la configuration                               ║
echo  ║  4️⃣  Cliquez sur "Démarrer le serveur"                                 ║
echo  ║  5️⃣  Téléchargez le fichier config.xml                                 ║
echo  ║  6️⃣  Placez config.xml dans votre dossier Dofus                        ║
echo  ║  7️⃣  Lancez Dofus et sélectionnez "localhost"                          ║
echo  ║  8️⃣  Connectez-vous et créez votre personnage !                        ║
echo  ║                                                                          ║
echo  ║  🎮 COMPTES DE TEST:                                                    ║
echo  ║  • admin / admin (Administrateur)                                       ║
echo  ║  • test / test (Joueur normal)                                          ║
echo  ║                                                                          ║
echo  ║  ⚠️  Appuyez sur Ctrl+C pour arrêter le serveur                        ║
echo  ║                                                                          ║
echo  ╚══════════════════════════════════════════════════════════════════════════╝
echo.

REM Attendre 3 secondes puis ouvrir le navigateur
echo 🌐 Ouverture du navigateur dans 3 secondes...
timeout /t 3 /nobreak >nul
start http://localhost:3000

REM Démarrer le serveur Node.js
echo 🚀 Serveur web en cours d'exécution...
echo.
node server.js

echo.
echo 🛑 Serveur arrêté
echo 👋 Merci d'avoir joué à Dofus Shivas !
pause
