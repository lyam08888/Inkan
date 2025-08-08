from flask import Flask, jsonify
from flask_cors import CORS
import subprocess
import os

app = Flask(__name__)
CORS(app)

@app.route('/')
def index():
    return app.send_static_file('index.html')

@app.route('/start-server')
def start_server():
    try:
        subprocess.Popen(['LANCER-SERVEUR-DOFUS.bat'], shell=True)
        return jsonify({'success': True, 'message': 'Serveur démarré'})
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

@app.route('/start-game')
def start_game():
    try:
        subprocess.Popen(['JOUER.bat'], shell=True)
        return jsonify({'success': True, 'message': 'Jeu démarré'})
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

if __name__ == '__main__':
    app.run(port=3000)
