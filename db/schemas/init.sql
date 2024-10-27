-- noinspection SqlNoDataSourceInspectionForFile

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- USERS

CREATE TABLE IF NOT EXISTS "user" (
  id SERIAL PRIMARY KEY,
  username VARCHAR(12) NOT NULL UNIQUE,
  -- SHA-1 hash of the password
  password bytea NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- If users is empty, insert a default user admin:admin
INSERT INTO "user" (username, password)
  SELECT 'admin', digest('admin', 'sha1')
  WHERE NOT EXISTS (SELECT 1 FROM "user");

-- PERMISSIONS

CREATE TABLE IF NOT EXISTS permissions (
  id SERIAL PRIMARY KEY,
  name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_permission (
  user_id INT NOT NULL,
  permission_id INT NOT NULL,
  PRIMARY KEY (user_id, permission_id),
  FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
  FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- If permissions is empty, insert a default permission admin and assign it to the default "user"
INSERT INTO permissions (name)
  SELECT 'admin'
  WHERE NOT EXISTS (SELECT 1 FROM permissions);

INSERT INTO user_permission (user_id, permission_id)
    SELECT "user".id, permissions.id
    FROM "user", permissions
    WHERE "user".username = 'admin' AND permissions.name = 'admin'
    AND NOT EXISTS (SELECT 1 FROM user_permission WHERE user_id = "user".id AND permission_id = permissions.id);

-- server

-- this tables stores the server that are currently online
-- server are made up of multiple nodes which are all synchronized with each other
CREATE TABLE IF NOT EXISTS server (
  id SERIAL PRIMARY KEY,
  type VARCHAR(4) NOT NULL, -- possible values: "game", "chat"
  name VARCHAR(32) NOT NULL UNIQUE,
  port INT NOT NULL, -- may actually be never used
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- specifies the necessary permissions for each server
CREATE TABLE IF NOT EXISTS server_permission (
  server_id INT NOT NULL,
  permission_id INT NOT NULL,
  PRIMARY KEY (server_id, permission_id),
  FOREIGN KEY (server_id) REFERENCES server(id) ON DELETE CASCADE,
  FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- this table stores the server that are currently online
-- each server is part of a server group it needs a unique ip and port combination
CREATE TABLE IF NOT EXISTS server_node (
  id SERIAL PRIMARY KEY,
  server_id INT NOT NULL,
  ipv4 INET NOT NULL,
  ipv6 INET NOT NULL,
  port INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (server_id) REFERENCES server(id) ON DELETE CASCADE,
  UNIQUE (ipv6, ipv4, port)
);

-- add a default server for the game server
INSERT INTO server (type, name, port)
  SELECT 'game', 'OpenMMO', 7777
  WHERE NOT EXISTS (SELECT 1 FROM server WHERE type = 'game' AND name = 'OpenMMO');

-- add a default server node for the OpenMMO server
INSERT INTO server_node (server_id, ipv4, ipv6, port)
  SELECT server.id, '127.0.0.1', '::1', 7777
  FROM server
  WHERE server.type = 'game' AND server.name = 'OpenMMO'
  AND NOT EXISTS (SELECT 1 FROM server_node WHERE server_id = server.id);

-- SERVER SESSIONS

-- tokens used to authenticate with the server
CREATE TABLE IF NOT EXISTS server_token (
  id SERIAL PRIMARY KEY,
  server_id INT NOT NULL,
  user_id INT NOT NULL,
  user_ip INET NOT NULL,
  token UUID NOT NULL DEFAULT gen_random_uuid(),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (server_id) REFERENCES server(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
  UNIQUE (token, server_id)
);

-- character

-- maybe we should store the skin data in separate tables?
CREATE TABLE IF NOT EXISTS character (
  id SERIAL PRIMARY KEY,
  user_id INT NOT NULL,
  name VARCHAR(32) NOT NULL,
  -- skin data
  forehead SMALLINT NOT NULL DEFAULT -1,
  forehead_color SMALLINT NOT NULL DEFAULT -1,
  hat SMALLINT NOT NULL DEFAULT -1,
  hat_color SMALLINT NOT NULL DEFAULT -1,
  hair SMALLINT NOT NULL DEFAULT -1,
  hair_color SMALLINT NOT NULL DEFAULT -1,
  eyes SMALLINT NOT NULL DEFAULT -1,
  eyes_color SMALLINT NOT NULL DEFAULT -1,
  facial_hair SMALLINT NOT NULL DEFAULT -1,
  facial_hair_color SMALLINT NOT NULL DEFAULT -1,
  back SMALLINT NOT NULL DEFAULT -1,
  back_color SMALLINT NOT NULL DEFAULT -1,
  top SMALLINT NOT NULL DEFAULT -1,
  top_color SMALLINT NOT NULL DEFAULT -1,
  gloves SMALLINT NOT NULL DEFAULT -1,
  gloves_color SMALLINT NOT NULL DEFAULT -1,
  footwear SMALLINT NOT NULL DEFAULT -1,
  footwear_color SMALLINT NOT NULL DEFAULT -1,
  leggings SMALLINT NOT NULL DEFAULT -1,
  leggings_color SMALLINT NOT NULL DEFAULT -1,
  fishing_rod SMALLINT NOT NULL DEFAULT -1,
  bike SMALLINT NOT NULL DEFAULT -1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE
);

-- default character for the default "user"
INSERT INTO character (user_id, name, forehead, forehead_color, hat, hat_color, hair, hair_color, eyes, eyes_color,
                       facial_hair, facial_hair_color, back, back_color, top, top_color, gloves, gloves_color,
                       footwear, footwear_color, leggings, leggings_color)
  SELECT "user".id, 'adminCharacter',
          0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
  FROM "user"
  WHERE "user".username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM character WHERE user_id = "user".id);

-- POKEMON & container

-- used to store pokemon e.g. players party or pc
CREATE TABLE IF NOT EXISTS container (
  id SERIAL PRIMARY KEY,
  name VARCHAR(32) NOT NULL UNIQUE,
  size INT NOT NULL,
  required BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO container (id, name, size, required)
  VALUES
   (0, 'pc', 660, TRUE),
   (1, 'party', 6, TRUE),
   (2, 'trade', 6, FALSE),
   (3, 'daycare', 21, FALSE),
   (4, 'rentalParty', 6, FALSE),
   (5, 'mail', 0, FALSE),
   (6, 'auction', 0, FALSE),
   (7, 'void', 0, FALSE),
   (8, 'deleted', 0, FALSE),
   (9, 'event', 6, FALSE)
  ON CONFLICT DO NOTHING;

-- TODO: create table with all moves and reference them here
CREATE TABLE IF NOT EXISTS pokemon (
  id SERIAL PRIMARY KEY,
  trainer_id INT NOT NULL,
  container_id INT NOT NULL,
  container_position INT NOT NULL,
  dex_id INT NOT NULL,
  original_trainer_id INT NOT NULL,
  caught_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  name VARCHAR(32) NOT NULL,
  seed INT NOT NULL DEFAULT (random() * (2147483647::BIGINT - (-2147483648)::BIGINT) + (-2147483648)::BIGINT)::INT,
  level INT NOT NULL,
  moves INT[4] NOT NULL,
  moves_pp INT[4] NOT NULL,
  ev_hp INT NOT NULL,
  ev_attack INT NOT NULL,
  ev_defense INT NOT NULL,
  ev_special_attack INT NOT NULL,
  ev_special_defense INT NOT NULL,
  ev_speed INT NOT NULL,
  iv_hp INT NOT NULL,
  iv_attack INT NOT NULL,
  iv_defense INT NOT NULL,
  iv_special_attack INT NOT NULL,
  iv_special_defense INT NOT NULL,
  iv_speed INT NOT NULL,
  is_shiny BOOLEAN NOT NULL DEFAULT FALSE,
  has_hidden_ability BOOLEAN NOT NULL DEFAULT FALSE,
  is_alpha BOOLEAN NOT NULL DEFAULT FALSE,
  is_secret BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (trainer_id) REFERENCES character(id) ON DELETE CASCADE,
  FOREIGN KEY (original_trainer_id) REFERENCES character(id) ON DELETE CASCADE,
  FOREIGN KEY (container_id) REFERENCES container(id) ON DELETE CASCADE
);

-- create a default pokemon for the default "user"
INSERT INTO pokemon (trainer_id, container_id, container_position, dex_id, original_trainer_id, name, level, moves, moves_pp,
                     ev_hp, ev_attack, ev_defense, ev_special_attack, ev_special_defense, ev_speed,
                     iv_hp, iv_attack, iv_defense, iv_special_attack, iv_special_defense, iv_speed,
                     is_shiny)
  SELECT character.id, container.id, 0, 150, character.id, 'adminPokemon', 100, ARRAY[1, 2, 3, 4], ARRAY[30, 30, 30, 30],
         31, 31, 31, 31, 31, 31,
         85, 85, 85, 85, 85, 85,
         TRUE
  FROM container, character
  WHERE container.name = 'party'
  AND character.name = 'adminCharacter'
  AND NOT EXISTS (SELECT 1 FROM pokemon WHERE container_id = container.id AND original_trainer_id = character.id);

-- ITEMS & INVENTORY

CREATE TABLE IF NOT EXISTS item (
  id INT PRIMARY KEY,
  name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS inventory (
  id SERIAL PRIMARY KEY,
  name VARCHAR(32) NOT NULL UNIQUE
);

INSERT INTO inventory (id, name) VALUES (0, 'void'), (1, 'inventory'), (2, 'warehouse'), (3, 'mail'), (4, 'temporary_event_inventory'), (5, 'temporary_shared_event_inventory')
  ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS owned_item (
  item_id INT NOT NULL,
  owner_id INT NOT NULL,
  inventory_id INT NOT NULL,
  amount INT NOT NULL,
  FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE,
  FOREIGN KEY (owner_id) REFERENCES character(id) ON DELETE CASCADE,
  FOREIGN KEY (inventory_id) REFERENCES inventory(id) ON DELETE CASCADE,
  CONSTRAINT item_owned_pk PRIMARY KEY (item_id, owner_id, inventory_id)
);


