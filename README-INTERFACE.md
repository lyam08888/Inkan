# 🎮 Interface Web Shivas - Serveur Dofus Hors Ligne

Cette interface web vous permet de gérer facilement votre serveur Dofus Shivas en mode hors ligne avec une interface graphique moderne.

## 🚀 Démarrage rapide

### Prérequis
- **Java 8+** installé sur votre système
- **Node.js** (version 14+) installé depuis [nodejs.org](https://nodejs.org/)
- **MariaDB/MySQL** (optionnel, une base SQLite sera créée automatiquement)

### Installation et lancement

1. **Double-cliquez** sur `start-shivas-interface.bat` (Windows)
   
   OU
   
   **Exécutez** `start-shivas-interface.ps1` dans PowerShell

2. **Ouvrez votre navigateur** et allez sur `http://localhost:3000`

3. **Suivez les étapes** dans l'interface web :
   - Cliquez sur "Configuration automatique"
   - Attendez la fin de la configuration
   - Cliquez sur "Démarrer le serveur"
   - Téléchargez le fichier `config.xml`

## 🎯 Fonctionnalités

### ⚙️ Configuration automatique
- ✅ Vérification de Java
- ✅ Configuration de la base de données
- ✅ Compilation du projet Shivas
- ✅ Configuration des fichiers
- ✅ Création des scripts de démarrage

### 🖥️ Interface web moderne
- 📊 Console en temps réel avec logs colorés
- 📈 Barre de progression pour les opérations
- 🔄 Vérification automatique du statut du serveur
- 🎨 Design moderne avec glassmorphism
- 📱 Interface responsive

### 🎮 Gestion du serveur
- 🚀 Démarrage/arrêt du serveur en un clic
- 📋 Informations de connexion affichées
- 🔍 Vérification automatique des ports
- 📥 Téléchargement automatique du config.xml

## 🔧 Configuration du client Dofus

1. **Téléchargez** le fichier `config.xml` depuis l'interface
2. **Placez-le** dans le dossier de votre client Dofus 1.29.1
3. **Lancez** Dofus et sélectionnez la configuration "localhost"
4. **Connectez-vous** avec n'importe quel nom d'utilisateur
5. **Créez** votre personnage et jouez !

## 📋 Informations de connexion

- **Serveur de connexion :** `127.0.0.1:6789`
- **Serveur de jeu :** `127.0.0.1:9876`
- **Version client requise :** `1.29.1`

## 🎯 Caractéristiques du serveur

- **Niveau de départ :** 200
- **Kamas de départ :** 1,000,000
- **Points d'action :** 6
- **Points de mouvement :** 3
- **Tous les waypoints :** Activés

## 🛠️ Dépannage

### Le serveur web ne démarre pas
- Vérifiez que Node.js est installé : `node --version`
- Vérifiez que le port 3000 n'est pas utilisé
- Exécutez `npm install` dans le dossier `web-server`

### Le serveur Shivas ne démarre pas
- Vérifiez que Java est installé : `java -version`
- Vérifiez que le projet est compilé
- Consultez les logs dans la console de l'interface

### Impossible de se connecter au jeu
- Vérifiez que le serveur est en ligne (statut vert)
- Vérifiez que le fichier `config.xml` est dans le bon dossier
- Vérifiez que vous utilisez Dofus version 1.29.1

## 📁 Structure des fichiers

```
c:\Users\Montoya\Inkan\
├── index.html                    # Interface web principale
├── start-shivas-interface.bat    # Script de démarrage Windows
├── start-shivas-interface.ps1    # Script de démarrage PowerShell
├── web-server\                   # Serveur web Node.js
│   ├── server.js                 # Serveur Express
│   ├── package.json              # Dépendances npm
│   └── node_modules\             # Modules Node.js
├── scripts\                      # Scripts PowerShell
│   ├── setup.ps1                 # Configuration automatique
│   ├── start-server.ps1          # Démarrage du serveur
│   └── check-server.ps1          # Vérification du statut
└── database\                     # Base de données
    └── shivas.sql                # Structure de la base
```

## 🎮 Comptes de test

Après la configuration, vous pouvez utiliser ces comptes :

- **Administrateur :** `admin` / `admin`
- **Joueur test :** `test` / `test`

## 🆘 Support

Si vous rencontrez des problèmes :

1. Consultez les logs dans la console de l'interface
2. Vérifiez que tous les prérequis sont installés
3. Redémarrez l'interface web
4. Vérifiez les ports utilisés (3000, 6789, 9876)

## 🎉 Bon jeu !

Votre serveur Dofus privé est maintenant prêt ! Amusez-vous bien dans l'univers de Dofus en mode hors ligne.
"@
</invoke>