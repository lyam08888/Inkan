@echo off
echo ========================================
echo    SERVEUR DOFUS SHIVAS - INTERFACE WEB
echo ========================================
echo.

cd /d "%~dp0"

echo Verification de Node.js...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERREUR: Node.js n'est pas installe ou n'est pas dans le PATH
    echo Veuillez installer Node.js depuis https://nodejs.org/
    pause
    exit /b 1
)

echo Node.js detecte avec succes
echo.

echo Installation des dependances...
cd web-server
if not exist node_modules (
    echo Installation de npm...
    npm install
    if %errorlevel% neq 0 (
        echo ERREUR: Impossible d'installer les dependances npm
        pause
        exit /b 1
    )
)

echo.
echo Demarrage du serveur web...
echo.
echo ========================================
echo  Interface web disponible sur:
echo  http://localhost:3000
echo ========================================
echo.
echo Appuyez sur Ctrl+C pour arreter le serveur
echo.

node server.js

pause