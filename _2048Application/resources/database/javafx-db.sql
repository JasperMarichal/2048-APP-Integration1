-- For creating the database
CREATE DATABASE ascii01;

-- To create the tables
CREATE TABLE IF NOT EXISTS INT_players(
    player_name varchar(20) constraint pk_players PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS INT_leaderboard (
    score numeric
    ,player_name varchar(20) constraint fk_player_name references INT_players(player_name)
    ,start_date date constraint nn_start_date not null
    ,duration interval constraint nn_duration not null
);

CREATE TABLE IF NOT EXISTS INT_board(
    board_id bigserial constraint pk_board_id primary key
    ,board_size numeric
);

CREATE TABLE IF NOT EXISTS INT_blocks(
    block_id bigserial constraint pk_block_id primary key
    ,block_value numeric not null
    ,block_x numeric(1) constraint nn_block_x not null
    ,block_y numeric(1) constraint nn_block_y not null
    ,board_id bigserial constraint fk_board_id references INT_board(board_id)
);

CREATE TABLE IF NOT EXISTS INT_games(
    game_id bigserial constraint pk_game_id primary key
    ,player_name varchar(20) constraint fk_player_name references INT_players(player_name)
    ,current_score numeric
    ,current_turn numeric
    ,board_id bigserial constraint fk_board_id references INT_board(board_id)
);
-- drop tables
DROP TABLE IF EXISTS INT_leaderboard cascade;
DROP TABLE IF EXISTS INT_players cascade;
DROP TABLE IF EXISTS INT_blocks cascade;
DROP TABLE IF EXISTS INT_board cascade;
DROP TABLE IF EXISTS INT_games cascade;

-- select all players
SELECT player_name FROM int_players;

-- select top 5 leaderboard
SELECT score, player_name, start_date, duration FROM int_leaderboard
ORDER BY score DESC, duration, start_date DESC
FETCH NEXT 5 ROWS ONLY;

-- select top 5 attempts of a specific player
SELECT score, player_name, start_date, duration FROM int_leaderboard
WHERE player_name ILIKE 'aaa'
ORDER BY score DESC, duration, start_date DESC
FETCH NEXT 5 ROWS ONLY;

-- insert test entries into leaderboard
INSERT INTO int_players values('Tomas') on conflict do nothing;
INSERT INTO int_leaderboard values (4637 ,'Tomas' ,current_date , interval '342 seconds') on conflict do nothing;
INSERT INTO int_players values('Koen') on conflict do nothing;
INSERT INTO int_leaderboard values (5474 ,'Koen' ,current_date , interval '213 seconds') on conflict do nothing;

-- remove test scoreboard entries from test players
DELETE FROM INT_leaderboard WHERE player_name IN ('Tomas', 'Koen', 'DemO');