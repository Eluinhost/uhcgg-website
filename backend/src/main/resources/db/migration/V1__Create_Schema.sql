-- setup uuid extension first
CREATE SCHEMA IF NOT EXISTS "uuid";
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA uuid;

CREATE TABLE roles (
  uuid        UUID    NOT NULL CONSTRAINT "PK__roles" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  name        TEXT    NOT NULL CONSTRAINT "UQ__roles__name" UNIQUE,
  permissions TEXT [] NOT NULL
);

CREATE TABLE users (
  uuid     UUID        NOT NULL CONSTRAINT "PK__users" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  username TEXT        NOT NULL CONSTRAINT "UQ__users__username" UNIQUE,
  email    TEXT        NOT NULL,
  password TEXT        NOT NULL,
  created  TIMESTAMPTZ NOT NULL                                    DEFAULT NOW(),
  modified TIMESTAMPTZ NOT NULL                                    DEFAULT NOW()
);

CREATE TABLE bans (
  uuid           UUID        NOT NULL CONSTRAINT "PK__bans" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  banned_user_id UUID        NOT NULL CONSTRAINT "FK__bans__users__banned_user_id" REFERENCES users (uuid),
  author_user_id UUID        NOT NULL CONSTRAINT "FK__bans__users__author_user_id" REFERENCES users (uuid),
  reason         TEXT        NOT NULL,
  created        TIMESTAMPTZ NOT NULL,
  modified       TIMESTAMPTZ NOT NULL,
  expires        TIMESTAMPTZ NOT NULL
);

CREATE TABLE user_roles (
  user_id UUID NOT NULL CONSTRAINT "FK__user_roles__users__user_id" REFERENCES users (uuid),
  role_id UUID NOT NULL CONSTRAINT "FK__user_roles__roles__role_id" REFERENCES roles (uuid)
);

CREATE TABLE scenarios (
  uuid          UUID        NOT NULL CONSTRAINT "PK__scenarios" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  name          TEXT        NOT NULL CONSTRAINT "UQ__scenarios__name" UNIQUE,
  description   TEXT        NOT NULL,
  created       TIMESTAMPTZ NOT NULL,
  modified      TIMESTAMPTZ NOT NULL,
  deleted       BOOLEAN     NOT NULL                                        DEFAULT FALSE,
  owner_user_id UUID        NOT NULL CONSTRAINT "FK__scenarios__users__owner_user_id" REFERENCES users (uuid)
);

CREATE TABLE networks (
  uuid          UUID        NOT NULL CONSTRAINT "PK__networks" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  name          TEXT        NOT NULL CONSTRAINT "UQ__networks__name" UNIQUE,
  tag           TEXT        NOT NULL CONSTRAINT "UQ__networks__tag" UNIQUE,
  description   TEXT        NOT NULL,
  created       TIMESTAMPTZ NOT NULL,
  modified      TIMESTAMPTZ NOT NULL,
  deleted       BOOLEAN     NOT NULL                                       DEFAULT FALSE,
  owner_user_id UUID        NOT NULL CONSTRAINT "FK__networks__users__owner_user_id" REFERENCES users (uuid)
);

CREATE TABLE network_permissions (
  network_id UUID    NOT NULL CONSTRAINT "FK__network_permissions__networks__network_id" REFERENCES networks (uuid),
  user_id    UUID    NOT NULL CONSTRAINT "FK__network_permissions__users__user_id" REFERENCES users (uuid),
  is_admin   BOOLEAN NOT NULL,
  CONSTRAINT UQ__network_permissions__network_id__user_id UNIQUE (network_id, user_id)
);

CREATE TABLE regions (
  uuid  UUID    NOT NULL CONSTRAINT "PK__regions" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  short CHAR(2) NOT NULL CONSTRAINT "UQ__regions__short" UNIQUE,
  long  TEXT    NOT NULL CONSTRAINT "UQ__regions__long" UNIQUE
);

CREATE TABLE servers (
  uuid          UUID        NOT NULL CONSTRAINT "PK__servers" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  owner_user_id UUID        NOT NULL CONSTRAINT "FK__servers__users__owner_user_id" REFERENCES users (uuid),
  network_id     UUID        NOT NULL CONSTRAINT "FK__servers_networks__network_id" REFERENCES networks (uuid),
  name          TEXT        NOT NULL CONSTRAINT "UQ__servers__name" UNIQUE,
  address       TEXT,
  ip            INET        NOT NULL,
  port          INT,
  location      TEXT        NOT NULL,
  region_id     UUID        NOT NULL CONSTRAINT "FK__servers__regions__region_id" REFERENCES regions (uuid),
  created       TIMESTAMPTZ NOT NULL,
  modified      TIMESTAMPTZ NOT NULL,
  deleted       BOOLEAN     NOT NULL                                      DEFAULT FALSE
);

CREATE TABLE versions (
  uuid UUID    NOT NULL CONSTRAINT "PK__versions" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  name TEXT    NOT NULL CONSTRAINT "UQ__versions__name" UNIQUE,
  live BOOLEAN NOT NULL                                       DEFAULT TRUE
);

CREATE TABLE styles (
  uuid          UUID    NOT NULL CONSTRAINT "PK__styles" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  short_name    TEXT    NOT NULL,
  full_name     TEXT    NOT NULL,
  description   TEXT    NOT NULL,
  requires_size BOOLEAN NOT NULL
);

CREATE TABLE matches (
  uuid         UUID        NOT NULL CONSTRAINT "PK__match" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  host_user_id UUID        NOT NULL CONSTRAINT "FK__matches__users__host_user_id" REFERENCES users (uuid),
  server_id    UUID        NOT NULL CONSTRAINT "FK__matches__servers__server_id" REFERENCES servers (uuid),
  version_id   UUID        NOT NULL CONSTRAINT "FK__matches__version__version_id" REFERENCES versions (uuid),
  style_id     UUID        NOT NULL CONSTRAINT "FK__matches__styles__style_id" REFERENCES styles (uuid),
  size         INT,
  created      TIMESTAMPTZ NOT NULL,
  modified     TIMESTAMPTZ NOT NULL,
  deleted      BOOLEAN     NOT NULL                                    DEFAULT FALSE,
  starts       TIMESTAMPTZ NOT NULL
);

CREATE TABLE match_scenarios (
  match_id    UUID NOT NULL CONSTRAINT "FK__match_scenarios__matches__match_id" REFERENCES matches (uuid),
  scenario_id UUID NOT NULL CONSTRAINT "FK__match_scenarios__scenarios__scenario_id" REFERENCES scenarios (uuid),
  CONSTRAINT "PK__match_scenarios" PRIMARY KEY (match_id, scenario_id)
);

----------
-- DATA --
----------

INSERT INTO regions (uuid, short, long) VALUES
  ('00000000-0000-0000-0000-000000000000', 'NA', 'North America'),
  ('00000000-0000-0000-0000-000000000001', 'EU', 'Europe'),
  ('00000000-0000-0000-0000-000000000002', 'OC', 'Oceania'),
  ('00000000-0000-0000-0000-000000000003', 'AS', 'Asia'),
  ('00000000-0000-0000-0000-000000000004', 'SA', 'South America'),
  ('00000000-0000-0000-0000-000000000005', 'AF', 'Africa');

INSERT INTO roles (uuid, name, permissions) VALUES
  ('00000000-0000-0000-0000-000000000000', 'default',
   '{"login", "scenario.create", "scenario.edit.own", "scenario.delete.own", "accounts.edit.own"}'),
  ('00000000-0000-0000-0000-000000000001', 'host',
   '{"network.create", "network.edit.own", "network.delete.own", "network.hosts.add.own", "network.hosts.remove.own", "network.owners.add.own", "network.owners.remove.own", "network.servers.add.own", "network.servers.remove.own", "servers.create", "servers.edit.own", "servers.delete.own", "matches.create", "matches.edit.own", "matches.delete.own"}'),
  ('00000000-0000-0000-0000-000000000002', 'mod',
   '{"network.edit", "network.delete", "network.hosts.add", "network.hosts.remove", "network.owners.add", "network.owners.remove", "network.servers.add", "network.servers.remove", "servers.edit", "servers.delete", "matches.edit", "matches.delete", "accounts.edit", "accounts.ban", "accounts.roles", "scenario.edit", "scenario.delete"}'),
  ('00000000-0000-0000-0000-000000000003', 'admin', '{"versions", "styles"}');

INSERT INTO styles (uuid, short_name, full_name, requires_size, description) VALUES
  ('00000000-0000-0000-0000-000000000000', 'FFA', 'Free for All', FALSE, 'TODO'), -- TODO
  ('00000000-0000-0000-0000-000000000001', 'cTo{{size}}', 'Chosen teams of {{size}}', TRUE, 'TODO'),
  ('00000000-0000-0000-0000-000000000002', 'rTo{{size}}', 'Random teams of {{size}}', TRUE, 'TODO'),
  ('00000000-0000-0000-0000-000000000003', 'CptTo{{size}}', 'Captains teams of {{size}}', TRUE, 'TODO'),
  ('00000000-0000-0000-0000-000000000004', 'pTo{{size}}', 'Picked teams of {{size}}', TRUE, 'TODO'),
  ('00000000-0000-0000-0000-000000000005', 'Market', 'Slave market', FALSE, 'TODO'),
  ('00000000-0000-0000-0000-000000000006', 'mTo{{size}}', 'Mystery teams of {{size}}', TRUE, 'TODO'),
  ('00000000-0000-0000-0000-000000000007', 'RvB', 'Red vs Blue', FALSE, 'TODO');

INSERT INTO versions (uuid, name, live) VALUES
  ('00000000-0000-0000-0000-000000000000', 'test', TRUE);