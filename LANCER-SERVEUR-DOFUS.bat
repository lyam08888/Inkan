@echo off
title Serveur Dofus Shivas - Interface Web
color 0A

echo.
echo  ╔══════════════════════════════════════════════════════════════╗
echo  ║                                                              ║
echo  ║           🎮 SERVEUR DOFUS SHIVAS - MODE HORS LIGNE 🎮       ║
echo  ║                                                              ║
echo  ║                    Interface Web Automatique                 ║
echo  ║                                                              ║
echo  ╚══════════════════════════════════════════════════════════════╝
echo.

cd /d "%~dp0"

echo [INFO] Verification des prerequis...
echo.

REM Vérifier Java
echo [JAVA] Verification de Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERREUR] Java n'est pas installe ou n'est pas dans le PATH
    echo.
    echo Veuillez installer Java 8+ depuis:
    echo https://www.oracle.com/java/technologies/downloads/
    echo.
    echo Ou utilisez OpenJDK depuis:
    echo https://adoptium.net/
    echo.
    pause
    exit /b 1
) else (
    echo [OK] Java detecte avec succes
)

REM Vérifier Node.js
echo [NODE] Verification de Node.js...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [AVERTISSEMENT] Node.js n'est pas installe
    echo.
    echo Tentative d'installation automatique de Node.js...
    echo.
    
    REM Télécharger et installer Node.js automatiquement
    powershell -Command "& {
        Write-Host 'Telechargement de Node.js...' -ForegroundColor Yellow;
        $url = 'https://nodejs.org/dist/v18.17.0/node-v18.17.0-x64.msi';
        $output = '$env:TEMP\nodejs.msi';
        try {
            Invoke-WebRequest -Uri $url -OutFile $output;
            Write-Host 'Installation de Node.js...' -ForegroundColor Yellow;
            Start-Process msiexec.exe -Wait -ArgumentList '/i', $output, '/quiet', '/norestart';
            Write-Host 'Node.js installe avec succes' -ForegroundColor Green;
        } catch {
            Write-Host 'Erreur lors du telechargement/installation' -ForegroundColor Red;
            Write-Host 'Veuillez installer Node.js manuellement depuis https://nodejs.org/' -ForegroundColor Yellow;
        }
    }"
    
    REM Actualiser les variables d'environnement
    call refreshenv >nul 2>&1
    
    REM Vérifier à nouveau
    node --version >nul 2>&1
    if %errorlevel% neq 0 (
        echo [ERREUR] Installation automatique echouee
        echo.
        echo Veuillez installer Node.js manuellement depuis:
        echo https://nodejs.org/
        echo.
        echo Puis relancez ce script.
        pause
        exit /b 1
    )
) else (
    echo [OK] Node.js detecte avec succes
)

echo.
echo [INFO] Preparation de l'environnement...

REM Créer les dossiers nécessaires
if not exist "web-server" mkdir web-server
if not exist "scripts" mkdir scripts
if not exist "data" mkdir data
if not exist "mods" mkdir mods
if not exist "database" mkdir database

echo [OK] Dossiers crees

REM Aller dans le dossier web-server
cd web-server

REM Installer les dépendances npm
if not exist "node_modules" (
    echo [NPM] Installation des dependances...
    npm install express cors
    if %errorlevel% neq 0 (
        echo [ERREUR] Impossible d'installer les dependances npm
        pause
        exit /b 1
    )
    echo [OK] Dependances installees
) else (
    echo [OK] Dependances deja installees
)

echo.
echo [INFO] Demarrage du serveur web...
echo.
echo  ╔══════════════════════════════════════════════════════════════╗
echo  ║                                                              ║
echo  ║  🌐 Interface web disponible sur: http://localhost:3000     ║
echo  ║                                                              ║
echo  ║  📋 Instructions:                                            ║
echo  ║  1. Ouvrez votre navigateur                                  ║
echo  ║  2. Allez sur http://localhost:3000                          ║
echo  ║  3. Cliquez sur "Configuration automatique"                 ║
echo  ║  4. Attendez la fin de la configuration                     ║
echo  ║  5. Cliquez sur "Demarrer le serveur"                       ║
echo  ║  6. Telechargez le config.xml                               ║
echo  ║  7. Lancez Dofus et connectez-vous !                        ║
echo  ║                                                              ║
echo  ║  ⚠️  Appuyez sur Ctrl+C pour arreter le serveur             ║
echo  ║                                                              ║
echo  ╚══════════════════════════════════════════════════════════════╝
echo.

REM Ouvrir automatiquement le navigateur
timeout /t 3 /nobreak >nul
start http://localhost:3000

REM Démarrer le serveur Node.js
node server.js

echo.
echo [INFO] Serveur arrete
pause