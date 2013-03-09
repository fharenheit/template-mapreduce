--------------------------------------------------------------------------------------------------------------
pig -Dmapred.job.name='Quantile Pig Job' quantile.pig
--------------------------------------------------------------------------------------------------------------

REGISTER lib/nomaven/datafu-0.0.8.jar;
define Quantile datafu.pig.stats.Quantile('0.1','0.5','0.7','0.8');

A = LOAD '/movielens_10m/ratings.dat' Using PigStorage(',') AS (user:chararray, movie:chararray, rating:int);
B = GROUP A BY movie;
C = FOREACH B { D = ORDER A BY rating DESC; GENERATE A.movie, Quantile(D.rating); };
D = LIMIT C 100;
DUMP D;

--------------------------------------------------------------------------------------------------------------
pig -Dmapred.job.name='Aggregation Count Pig Job' quantile.pig
--------------------------------------------------------------------------------------------------------------

A = LOAD '/movielens_10m/ratings.dat' Using PigStorage(',') AS (user:chararray, movie:chararray, rating:int);
B = GROUP A BY movie;
C = FOREACH B { D = ORDER A BY rating DESC; GENERATE movie, COUNT(D); };
DUMP C;
