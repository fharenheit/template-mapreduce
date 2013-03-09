REGISTER lib/piggybank-0.11.0.jar;
define Quantile datafu.pig.stats.Quantile('0.1','0.5','0.7','0.8');

A = LOAD '/movielens_10m/ratings.dat' Using PigStorage(',') AS (user:chararray, movie:chararray, rating:int);
B = GROUP A BY movie;
C = FOREACH B { D = ORDER A BY rating DESC; GENERATE Quantile(D); };
DUMP C;

--------------------------------------------------------------------------------------------------------------

A = LOAD '/movielens_10m/ratings.dat' Using PigStorage(',') AS (user:chararray, movie:chararray, rating:int);
B = GROUP A BY movie;
C = FOREACH B { D = ORDER A BY rating DESC; GENERATE COUNT(D); };
DUMP C;
