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
