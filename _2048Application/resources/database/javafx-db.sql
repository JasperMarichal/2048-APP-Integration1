-- For creating the database
CREATE DATABASE ascii01;

-- To create the tables
create table INT_players(
    player_name varchar(20) constraint pk_players PRIMARY KEY
);

create table INT_leaderboard (
    score numeric
    ,player_name varchar(20) constraint fk_player_name references INT_players(player_name)
    ,start_date date constraint nn_start_date not null
    ,duration interval constraint nn_duration not null
);

-- drop tables
drop table int_leaderboard cascade;
drop table int_players cascade;

-- select all players
SELECT player_name FROM int_players;

-- select top 5 leaderboard
SELECT player_name, score, duration FROM int_leaderboard
ORDER BY score DESC, duration, start_date DESC
FETCH NEXT 5 ROWS ONLY;

-- select top 5 attempts of a specific player
SELECT player_name, score, duration FROM int_leaderboard
WHERE player_name ILIKE 'aaa'
ORDER BY score DESC, duration, start_date DESC
FETCH NEXT 5 ROWS ONLY;

-- insert test entries into leaderboard
insert into int_players values('hello');
INSERT INTO int_leaderboard values (4637 ,'hello' ,current_date , interval '342 seconds');
insert into int_players values('aaa');
INSERT INTO int_leaderboard values (5474 ,'aaa' ,current_date , interval '213 seconds');
