Set oWS = WScript.CreateObject("WScript.Shell")
sLinkFile = oWS.ExpandEnvironmentStrings("%USERPROFILE%\Desktop\Dofus 1.29.1.lnk")
Set oLink = oWS.CreateShortcut(sLinkFile)

' Chemin vers le fichier LAUNCHER.bat
scriptdir = CreateObject("Scripting.FileSystemObject").GetParentFolderName(WScript.ScriptFullName)
oLink.TargetPath = scriptdir & "\LAUNCHER.bat"

' Icône personnalisée (utilise l'icône de cmd.exe par défaut)
oLink.IconLocation = "%SystemRoot%\System32\cmd.exe,0"

' Description qui apparaît au survol
oLink.Description = "Launcher Dofus 1.29.1"

' Démarrer dans le dossier du launcher
oLink.WorkingDirectory = scriptdir

oLink.Save
