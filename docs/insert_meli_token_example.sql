
-- Script to insert the initial Mercado Libre token into the meli_token table
-- Replace the quoted values with those obtained from the OAuth flow
-- Run this script in your database before starting the app for the first time

INSERT INTO meli_token (access_token, refresh_token, expires_in)
VALUES (
  'YOUR_ACCESS_TOKEN_HERE',
  'YOUR_REFRESH_TOKEN_HERE',
  '2025-08-01 18:00:00' -- Use the real expiration date/time (YYYY-MM-DD HH:MI:SS)
);

-- You can delete this record and re-insert it if you need to regenerate the tokens.
