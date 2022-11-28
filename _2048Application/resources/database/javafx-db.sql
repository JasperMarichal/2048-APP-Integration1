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

SELECT * from int_leaderboard
group by player_name, score, start_date, duration
order by player_name
fetch next 5 rows only;
insert into int_players values('hello');
INSERT INTO int_leaderboard values (4637 ,'hello' ,current_date , interval '11 minutes 2 seconds');
insert into int_players values('aaa');
INSERT INTO int_leaderboard values (5474 ,'aaa' ,current_date , interval '30 minutes 2 seconds');