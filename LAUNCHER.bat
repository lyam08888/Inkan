@echo off
title Dofus 1.29.1 Launcher
color 0A

:menu
cls
echo ======================================
echo         DOFUS 1.29.1 LAUNCHER
echo ======================================
echo.
echo   1. DEMARRER LE SERVEUR
echo   2. LANCER LE JEU
echo   3. QUITTER
echo.
echo ======================================

set /p choix="Votre choix (1-3): "

if "%choix%"=="1" (
    echo.
    echo Demarrage du serveur...
    start "" "LANCER-SERVEUR-DOFUS.bat"
    timeout /t 2 >nul
    goto menu
)

if "%choix%"=="2" (
    echo.
    echo Lancement du jeu...
    start "" "JOUER.bat"
    timeout /t 2 >nul
    goto menu
)

if "%choix%"=="3" (
    exit
)

goto menu
