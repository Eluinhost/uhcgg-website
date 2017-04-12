-- setup uuid extension first
CREATE SCHEMA IF NOT EXISTS "uuid";
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA uuid;

-- Holds short lived authorisation state info whilst sending off to 3rd party authorisation endpoints
-- CREATE TABLE IF NOT EXISTS "ExternalOauthState" (
--   id BIGSERIAL NOT NULL CONSTRAINT "PK_ExternalOauthState" PRIMARY KEY,
--   state UUID NOT NULL DEFAULT uuid.uuid_generate_v4(),
--   ip INET NOT NULL,
--   scopes TEXT[] NOT NULL CONSTRAINT "CK_ExternalOauthAuthorisations_scope" CHECK (array_length(scopes, 1) IS NOT NULL),
--   expires TIMESTAMPTZ NOT NULL
-- );
--
-- -- Holds actual oauth tokens after granted access for the user
-- CREATE TABLE IF NOT EXISTS "ExternalOauthAccessToken" (
--   id BIGSERIAL NOT NULL CONSTRAINT "PK_ExternalOauthAccessToken" PRIMARY KEY,
--   access_token TEXT NOT NULL,
--   token_type TEXT NOT NULL,
--   expires TIMESTAMPTZ NOT NULL,
--   scope TEXT[] NOT NULL,
--   refresh_token TEXT NOT NULL
-- );