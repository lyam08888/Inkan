@echo off
color 0A
cls
echo.
echo   ==========================================
echo    DOFUS LAUNCHER - VERSION HORS LIGNE
echo   ==========================================
echo.
echo   1. Demarrer le serveur
echo   2. Lancer le jeu
echo   3. Quitter
echo.
set /p choix="Votre choix (1-3) : "

if "%choix%"=="1" (
    cls
    echo Demarrage du serveur...
    call start-server.bat
    pause
    goto :menu
)
if "%choix%"=="2" (
    cls
    echo Lancement du jeu...
    call start-game.bat
    pause
    goto :menu
)
if "%choix%"=="3" (
    exit
)

:menu
cls
goto :start
