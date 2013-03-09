--------------------------------------------------------------------------------------------------------------
pig -Dmapred.job.name='Quantile Pig Job' quantile.pig
--------------------------------------------------------------------------------------------------------------

REGISTER lib/nomaven/datafu-0.0.8.jar;
define Quantile datafu.pig.stats.Quantile('0.01','0.1','0.2','0.3','0.4','0.5','0.6','0.7','0.8','0.9','0.99');

A = LOAD '/movielens_10m/ratings.dat' Using PigStorage(',') AS (user:chararray, movie:chararray, rating:int);
B = GROUP A BY movie;
C = FOREACH B { D = ORDER A BY rating DESC; GENERATE B.group, Quantile(D.rating); };
DESCRIBE C;

D = LIMIT C 100;
DUMP D;

입력 : 사용자,영화,평점
출력 : 영화,Percentile(0.01~0.99)

--------------------------------------------------------------------------------------------------------------
pig -Dmapred.job.name='Aggregation Count Pig Job' quantile.pig
--------------------------------------------------------------------------------------------------------------

A = LOAD '/movielens_10m/ratings.dat' Using PigStorage(',') AS (user:chararray, movie:chararray, rating:int);
B = GROUP A BY movie;
C = FOREACH B { D = ORDER A BY rating DESC; GENERATE B.group, COUNT(D); };
DUMP C;
