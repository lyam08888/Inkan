const express = require('express');
const { spawn, exec } = require('child_process');
const path = require('path');
const fs = require('fs');
const cors = require('cors');

const app = express();
const PORT = 3000;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.static(path.join(__dirname, '..')));

// Variables globales
let serverProcess = null;
let serverStatus = 'offline';

// Routes API
app.get('/api/status', (req, res) => {
    // Exécuter le script de vérification du statut
    exec('powershell -ExecutionPolicy Bypass -File "scripts/check-server.ps1"', 
         { cwd: path.join(__dirname, '..') }, 
         (error, stdout, stderr) => {
        if (error) {
            res.json({ status: 'offline', error: error.message });
            return;
        }
        
        try {
            const status = JSON.parse(stdout.trim());
            res.json(status);
        } catch (e) {
            res.json({ status: 'offline', error: 'Failed to parse status' });
        }
    });
});

app.post('/api/setup', (req, res) => {
    const setupProcess = spawn('powershell', [
        '-ExecutionPolicy', 'Bypass',
        '-File', 'scripts/setup.ps1'
    ], { 
        cwd: path.join(__dirname, '..'),
        stdio: 'pipe'
    });

    let output = '';
    
    setupProcess.stdout.on('data', (data) => {
        output += data.toString();
    });
    
    setupProcess.stderr.on('data', (data) => {
        output += data.toString();
    });
    
    setupProcess.on('close', (code) => {
        res.json({
            success: code === 0,
            output: output,
            exitCode: code
        });
    });
});

app.post('/api/start', (req, res) => {
    if (serverProcess) {
        res.json({ success: false, message: 'Server is already running' });
        return;
    }

    serverProcess = spawn('powershell', [
        '-ExecutionPolicy', 'Bypass',
        '-File', 'scripts/start-server.ps1'
    ], { 
        cwd: path.join(__dirname, '..'),
        stdio: 'pipe'
    });

    let output = '';
    
    serverProcess.stdout.on('data', (data) => {
        output += data.toString();
        console.log('Server output:', data.toString());
    });
    
    serverProcess.stderr.on('data', (data) => {
        output += data.toString();
        console.error('Server error:', data.toString());
    });
    
    serverProcess.on('close', (code) => {
        console.log(`Server process exited with code ${code}`);
        serverProcess = null;
        serverStatus = 'offline';
    });

    // Attendre un peu pour que le serveur démarre
    setTimeout(() => {
        serverStatus = 'online';
        res.json({ 
            success: true, 
            message: 'Server starting...',
            pid: serverProcess.pid 
        });
    }, 2000);
});

app.post('/api/stop', (req, res) => {
    if (!serverProcess) {
        res.json({ success: false, message: 'Server is not running' });
        return;
    }

    // Tuer le processus du serveur
    try {
        process.kill(serverProcess.pid, 'SIGTERM');
        serverProcess = null;
        serverStatus = 'offline';
        res.json({ success: true, message: 'Server stopped' });
    } catch (error) {
        res.json({ success: false, message: 'Failed to stop server: ' + error.message });
    }
});

// Route pour servir l'index.html
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, '..', 'index.html'));
});

// Démarrer le serveur web
app.listen(PORT, () => {
    console.log(`🌐 Serveur web Shivas démarré sur http://localhost:${PORT}`);
    console.log(`📁 Dossier racine: ${path.join(__dirname, '..')}`);
    console.log(`🎮 Ouvrez votre navigateur et allez sur http://localhost:${PORT}`);
});

// Gestion propre de l'arrêt
process.on('SIGINT', () => {
    console.log('\n🛑 Arrêt du serveur web...');
    if (serverProcess) {
        console.log('🛑 Arrêt du serveur Shivas...');
        process.kill(serverProcess.pid, 'SIGTERM');
    }
    process.exit(0);
});
"@
</invoke>