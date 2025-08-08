@echo off
mode con: cols=60 lines=25
title DOFUS 1.29.1
color 1F

:MENU
cls
echo.
echo    ====================================
echo            DOFUS 1.29.1
echo    ====================================
echo.
echo    1 - DEMARRER LE SERVEUR
echo    2 - LANCER LE JEU
echo    3 - QUITTER
echo.
echo    ====================================
echo.
set /p choix="    Choix (1,2,3) : "

if "%choix%"=="1" goto SERVER
if "%choix%"=="2" goto GAME
if "%choix%"=="3" exit

goto MENU

:SERVER
cls
echo.
echo    Demarrage du serveur...
echo    ====================================
echo.
start /WAIT CMD /C LANCER-SERVEUR-DOFUS.bat
echo.
echo    Le serveur est en cours d'execution!
echo    Vous pouvez maintenant lancer le jeu.
echo.
pause
goto MENU

:GAME
cls
echo.
echo    Verification du jeu...
echo    ====================================
echo.

if not exist "JOUER.bat" (
    echo    ERREUR: JOUER.bat non trouve!
    echo    Verifiez que le fichier existe.
    echo.
    pause
    goto MENU
)

echo    Lancement du jeu...
echo.
start /WAIT CMD /C JOUER.bat

if errorlevel 1 (
    echo    ERREUR: Probleme lors du lancement du jeu
    echo    Code d'erreur: %errorlevel%
) else (
    echo    Le jeu a ete lance avec succes!
)

echo.
echo    Si le jeu ne se lance pas:
echo    1. Verifiez que le serveur est bien demarre
echo    2. Verifiez que JOUER.bat est correct
echo    3. Essayez de relancer le launcher
echo.
pause
goto MENU
