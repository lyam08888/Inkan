const express = require('express');
const { exec } = require('child_process');
const path = require('path');
const cors = require('cors');
const app = express();
const port = 3000;

app.use(cors());
app.use(express.static(path.join(__dirname)));

app.get('/start-server', (req, res) => {
    exec('LANCER-SERVEUR-DOFUS.bat', (error, stdout, stderr) => {
        if (error) {
            console.error(`Erreur d'exécution: ${error}`);
            return res.status(500).json({ error: error.message });
        }
        res.json({ success: true, message: 'Serveur démarré' });
    });
});

app.get('/start-game', (req, res) => {
    exec('JOUER.bat', (error, stdout, stderr) => {
        if (error) {
            console.error(`Erreur d'exécution: ${error}`);
            return res.status(500).json({ error: error.message });
        }
        res.json({ success: true, message: 'Jeu démarré' });
    });
});

app.listen(port, () => {
    console.log(`Serveur lancé sur http://localhost:${port}`);
});
