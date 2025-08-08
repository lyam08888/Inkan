@echo off
chcp 65001 >nul
title 🎮 Dofus Shivas - Interface Tout-en-Un
color 0B

cls
echo.
echo  ╔══════════════════════════════════════════════════════════════════════════╗
echo  ║                                                                          ║
echo  ║    🎮 DOFUS SHIVAS - INTERFACE TOUT-EN-UN 🎮                            ║
echo  ║                                                                          ║
echo  ║    ⚡ Tout se lance depuis votre navigateur !                           ║
echo  ║    📁 Aucun serveur externe requis                                      ║
echo  ║    🚀 Interface 100%% autonome                                           ║
echo  ║                                                                          ║
echo  ║    📋 Cette interface va :                                              ║
echo  ║    • Télécharger tous les scripts nécessaires                          ║
echo  ║    • Configurer automatiquement le serveur                             ║
echo  ║    • Générer les fichiers de démarrage                                 ║
echo  ║    • Fournir le config.xml pour Dofus                                  ║
echo  ║                                                                          ║
echo  ╚══════════════════════════════════════════════════════════════════════════╝
echo.

cd /d "%~dp0"

echo 🌐 Ouverture de l'interface Shivas...
echo.
echo  ╔══════════════════════════════════════════════════════════════════════════╗
echo  ║                                                                          ║
echo  ║  🎯 INTERFACE OUVERTE DANS VOTRE NAVIGATEUR !                          ║
echo  ║                                                                          ║
echo  ║  📋 ÉTAPES À SUIVRE :                                                   ║
echo  ║                                                                          ║
echo  ║  1️⃣  Cliquez sur "Configuration automatique"                           ║
echo  ║      → Télécharge tous les scripts nécessaires                         ║
echo  ║                                                                          ║
echo  ║  2️⃣  Exécutez les scripts téléchargés dans l'ordre :                   ║
echo  ║      → install-java.ps1 (si Java manque)                               ║
echo  ║      → setup-database.ps1                                              ║
echo  ║      → compile-project.ps1                                             ║
echo  ║      → setup-config.ps1                                                ║
echo  ║                                                                          ║
echo  ║  3️⃣  Cliquez sur "Démarrer le serveur"                                 ║
echo  ║      → Télécharge start-shivas-server.ps1                              ║
echo  ║                                                                          ║
echo  ║  4️⃣  Exécutez start-shivas-server.ps1                                  ║
echo  ║      → Lance le serveur Dofus                                           ║
echo  ║                                                                          ║
echo  ║  5️⃣  Téléchargez config.xml depuis l'interface                         ║
echo  ║      → Placez-le dans votre dossier Dofus                              ║
echo  ║                                                                          ║
echo  ║  6️⃣  Lancez Dofus et connectez-vous !                                  ║
echo  ║      → Sélectionnez "localhost"                                         ║
echo  ║      → Utilisez admin/admin ou test/test                               ║
echo  ║                                                                          ║
echo  ║  🎮 TOUT EST AUTONOME - PAS BESOIN DE SERVEUR WEB !                    ║
echo  ║                                                                          ║
echo  ╚══════════════════════════════════════════════════════════════════════════╝
echo.

REM Ouvrir l'index.html directement dans le navigateur
echo 🚀 Lancement de l'interface...
start "" "index.html"

echo.
echo ✅ Interface lancée avec succès !
echo 📋 Suivez les instructions dans votre navigateur
echo.
echo 🔄 Cette fenêtre peut être fermée
echo 📁 Tous les scripts seront téléchargés automatiquement
echo.
pause