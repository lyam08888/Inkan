#NoEnv
#SingleInstance Force
SetWorkingDir %A_ScriptDir%

; Configuration de l'interface graphique
Gui, Color, 1a1a2e
Gui, +AlwaysOnTop -SysMenu

; Styles personnalisés
buttonWidth := 300
buttonHeight := 50
windowWidth := 400
windowPadding := (windowWidth - buttonWidth) / 2

; Ajout des éléments
Gui, Font, s24 cWhite, Segoe UI
Gui, Add, Text, x0 w%windowWidth% Center, 🎮 Dofus 1.29.1

Gui, Font, s12 c4ecdc4
Gui, Add, Text, x0 w%windowWidth% y+20 Center vStatusText, Prêt à démarrer

; Boutons avec dégradés
Gui, Font, s12 Bold cWhite
Gui, Add, Button, x%buttonPadding% y+20 w%buttonWidth% h%buttonHeight% gStartServer, 🚀 DÉMARRER LE SERVEUR
Gui, Add, Button, x%buttonPadding% y+10 w%buttonWidth% h%buttonHeight% gStartGame, ⚔️ LANCER LE JEU

; Zone de console
Gui, Font, s10 Normal c4ecdc4, Consolas
Gui, Add, Edit, x%buttonPadding% y+20 w%buttonWidth% h100 vConsole ReadOnly, Bienvenue! Cliquez sur "DÉMARRER LE SERVEUR" pour commencer.

; Affichage de la fenêtre
Gui, Show, w%windowWidth% h400, Dofus Launcher

serverRunning := false
return

GuiClose:
ExitApp

StartServer:
    if (!serverRunning) {
        GuiControl,, StatusText, ⏳ Démarrage du serveur...
        AppendConsole("Lancement du serveur Dofus...")
        Run, LANCER-SERVEUR-DOFUS.bat,, Hide
        SetTimer, ServerStarted, -2000
    }
return

ServerStarted:
    serverRunning := true
    GuiControl,, StatusText, ✅ Serveur en ligne
    AppendConsole("✅ Serveur démarré avec succès!")
return

StartGame:
    if (!serverRunning) {
        MsgBox, 48, Attention, ⚠️ Veuillez d'abord démarrer le serveur!
        return
    }
    AppendConsole("Lancement du jeu...")
    Run, JOUER.bat,, Hide
    AppendConsole("✅ Jeu lancé!")
return

AppendConsole(text) {
    FormatTime, time,, HH:mm:ss
    GuiControlGet, current,, Console
    if (current)
        text := current . "`n[" . time . "] " . text
    else
        text := "[" . time . "] " . text
    GuiControl,, Console, %text%
}
