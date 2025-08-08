@echo off
chcp 65001 > nul
title DOFUS 1.29.1
color 0B

:MENU
cls
echo.
echo    ██████╗  ██████╗ ███████╗██╗   ██╗███████╗
echo    ██╔══██╗██╔═══██╗██╔════╝██║   ██║██╔════╝
echo    ██║  ██║██║   ██║█████╗  ██║   ██║███████╗
echo    ██║  ██║██║   ██║██╔══╝  ██║   ██║╚════██║
echo    ██████╔╝╚██████╔╝██║     ╚██████╔╝███████║
echo    ╚═════╝  ╚═════╝ ╚═╝      ╚═════╝ ╚══════╝
echo            version 1.29.1
echo.
echo ═══════════════════════════════════════════════════
echo.
echo   [1] DÉMARRER LE SERVEUR
echo   [2] LANCER LE JEU
echo   [3] QUITTER
echo.
echo ═══════════════════════════════════════════════════
echo.

set /p choix="Votre choix (1-3): "

if "%choix%"=="1" (
    cls
    echo.
    echo  Démarrage du serveur...
    echo.
    start "" "LANCER-SERVEUR-DOFUS.bat"
    timeout /t 2 >nul
    echo  ✓ Serveur démarré!
    echo.
    pause
    goto MENU
)

if "%choix%"=="2" (
    cls
    echo.
    echo  Lancement du jeu...
    echo.
    start "" "JOUER.bat"
    timeout /t 2 >nul
    echo  ✓ Jeu lancé!
    echo.
    pause
    goto MENU
)

if "%choix%"=="3" (
    exit
)

goto MENU
