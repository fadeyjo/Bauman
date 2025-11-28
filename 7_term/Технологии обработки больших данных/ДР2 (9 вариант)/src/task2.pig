TWEETS = LOAD 'input/tweets.csv' USING PigStorage(',') AS (tweet_id:int, tweet:chararray, login:chararray);
USERS = LOAD 'input/users.csv' USING PigStorage(',') AS (login:chararray, user_name:chararray, state:chararray);

TWEET_COUNT = GROUP TWEETS BY login;
USER_TWEETS = FOREACH TWEET_COUNT GENERATE
    group AS login,
    COUNT(TWEETS) AS tweet_count;

FEW_TWEETS = FILTER USER_TWEETS BY tweet_count < 3;

FEW_USERS = JOIN FEW_TWEETS BY login, USERS BY login;

FEW_USERS_FINAL = FOREACH FEW_USERS GENERATE
    FEW_TWEETS::login AS login,
    USERS::user_name AS user_name,
    FEW_TWEETS::tweet_count AS tweet_count;

FEW_TWEETS_SUM = FOREACH (GROUP FEW_USERS_FINAL ALL) GENERATE SUM(FEW_USERS_FINAL.tweet_count) AS few_total;

TOTAL_TWEETS = FOREACH (GROUP TWEETS ALL) GENERATE COUNT(TWEETS) AS total;

JOINED = CROSS FEW_TWEETS_SUM, TOTAL_TWEETS;
RESULT_STATS = FOREACH JOINED GENERATE
    few_total,
    total,
    (double)few_total / (double)total AS fraction;

STORE RESULT_STATS INTO 'output/task2_stats_2025-10-20' USING PigStorage(',');

STORE FEW_USERS_FINAL INTO 'output/task2_users_2025-10-20' USING PigStorage(',');
