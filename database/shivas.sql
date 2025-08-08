-- Base de données Shivas pour serveur Dofus privé
-- Version: 1.0
-- Compatible avec Shivas 1.0-SNAPSHOT

-- Table des comptes utilisateurs
CREATE TABLE IF NOT EXISTS accounts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(255),
    secret_question VARCHAR(255),
    secret_answer VARCHAR(255),
    rights INTEGER DEFAULT 0,
    banned BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    last_ip VARCHAR(45)
);

-- Table des personnages
CREATE TABLE IF NOT EXISTS players (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_id INTEGER NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    breed INTEGER NOT NULL,
    gender BOOLEAN NOT NULL,
    colors VARCHAR(255),
    level INTEGER DEFAULT 200,
    experience BIGINT DEFAULT 0,
    kamas BIGINT DEFAULT 1000000,
    map_id INTEGER DEFAULT 7411,
    cell_id INTEGER DEFAULT 255,
    direction INTEGER DEFAULT 1,
    skin INTEGER DEFAULT 0,
    size INTEGER DEFAULT 100,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- Table des statistiques des personnages
CREATE TABLE IF NOT EXISTS player_stats (
    player_id INTEGER PRIMARY KEY,
    vitality INTEGER DEFAULT 101,
    wisdom INTEGER DEFAULT 25,
    strength INTEGER DEFAULT 101,
    intelligence INTEGER DEFAULT 101,
    chance INTEGER DEFAULT 101,
    agility INTEGER DEFAULT 101,
    action_points INTEGER DEFAULT 6,
    movement_points INTEGER DEFAULT 3,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);

-- Table des sorts des personnages
CREATE TABLE IF NOT EXISTS player_spells (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id INTEGER NOT NULL,
    spell_id INTEGER NOT NULL,
    level INTEGER DEFAULT 1,
    position INTEGER DEFAULT 0,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);

-- Table des objets des personnages
CREATE TABLE IF NOT EXISTS player_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id INTEGER NOT NULL,
    item_id INTEGER NOT NULL,
    quantity INTEGER DEFAULT 1,
    position INTEGER DEFAULT -1,
    effects TEXT,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);

-- Table des guildes
CREATE TABLE IF NOT EXISTS guilds (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(255) UNIQUE NOT NULL,
    emblem INTEGER DEFAULT 0,
    level INTEGER DEFAULT 1,
    experience BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des membres de guilde
CREATE TABLE IF NOT EXISTS guild_members (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    guild_id INTEGER NOT NULL,
    player_id INTEGER NOT NULL,
    rank INTEGER DEFAULT 0,
    experience_given BIGINT DEFAULT 0,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guild_id) REFERENCES guilds(id) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);

-- Table des amis
CREATE TABLE IF NOT EXISTS friends (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id INTEGER NOT NULL,
    friend_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES players(id) ON DELETE CASCADE
);

-- Table des ennemis
CREATE TABLE IF NOT EXISTS enemies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id INTEGER NOT NULL,
    enemy_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
    FOREIGN KEY (enemy_id) REFERENCES players(id) ON DELETE CASCADE
);

-- Insérer des comptes de test
INSERT OR IGNORE INTO accounts (name, password, nickname, rights) VALUES 
('admin', 'admin', 'Administrateur', 1),
('test', 'test', 'Joueur Test', 0),
('demo', 'demo', 'Démonstration', 0);

-- Insérer un personnage de test pour l'admin
INSERT OR IGNORE INTO players (account_id, name, breed, gender, colors, level, kamas) VALUES 
(1, 'Admin', 1, 1, '16777215,16777215,16777215,16777215,16777215', 200, 10000000);

-- Insérer les stats du personnage admin
INSERT OR IGNORE INTO player_stats (player_id, vitality, wisdom, strength, intelligence, chance, agility, action_points, movement_points) VALUES 
(1, 500, 100, 500, 500, 500, 500, 12, 6);

-- Insérer quelques sorts de base pour le personnage admin (Iop)
INSERT OR IGNORE INTO player_spells (player_id, spell_id, level, position) VALUES 
(1, 1, 6, 1),  -- Compulsion
(1, 2, 6, 2),  -- Pression
(1, 3, 6, 3),  -- Intimidation
(1, 4, 6, 4),  -- Concentration
(1, 5, 6, 5),  -- Vitalité
(1, 6, 6, 6);  -- Épée Divine

-- Créer des index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_accounts_name ON accounts(name);
CREATE INDEX IF NOT EXISTS idx_players_account_id ON players(account_id);
CREATE INDEX IF NOT EXISTS idx_players_name ON players(name);
CREATE INDEX IF NOT EXISTS idx_player_spells_player_id ON player_spells(player_id);
CREATE INDEX IF NOT EXISTS idx_player_items_player_id ON player_items(player_id);
CREATE INDEX IF NOT EXISTS idx_guild_members_guild_id ON guild_members(guild_id);
CREATE INDEX IF NOT EXISTS idx_guild_members_player_id ON guild_members(player_id);

-- Vues utiles pour les requêtes
CREATE VIEW IF NOT EXISTS player_full_info AS
SELECT 
    p.*,
    a.name as account_name,
    a.rights as account_rights,
    ps.vitality, ps.wisdom, ps.strength, ps.intelligence, ps.chance, ps.agility,
    ps.action_points, ps.movement_points
FROM players p
JOIN accounts a ON p.account_id = a.id
LEFT JOIN player_stats ps ON p.id = ps.player_id;

-- Triggers pour maintenir la cohérence
CREATE TRIGGER IF NOT EXISTS create_player_stats
AFTER INSERT ON players
BEGIN
    INSERT INTO player_stats (player_id, vitality, wisdom, strength, intelligence, chance, agility, action_points, movement_points)
    VALUES (NEW.id, 101, 25, 101, 101, 101, 101, 6, 3);
END;

-- Données de configuration
CREATE TABLE IF NOT EXISTS server_config (
    key VARCHAR(255) PRIMARY KEY,
    value TEXT,
    description TEXT
);

INSERT OR IGNORE INTO server_config (key, value, description) VALUES 
('server_name', 'Shivas Private Server', 'Nom du serveur'),
('max_players', '100', 'Nombre maximum de joueurs'),
('exp_rate', '1.0', 'Multiplicateur d\'expérience'),
('drop_rate', '1.0', 'Multiplicateur de drop'),
('kamas_rate', '1.0', 'Multiplicateur de kamas'),
('start_level', '200', 'Niveau de départ'),
('start_kamas', '1000000', 'Kamas de départ'),
('start_map', '7411', 'Carte de départ'),
('start_cell', '255', 'Cellule de départ');

PRAGMA foreign_keys = ON;
PRAGMA journal_mode = WAL;
PRAGMA synchronous = NORMAL;