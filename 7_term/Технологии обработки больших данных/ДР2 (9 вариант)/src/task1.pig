STOPWORDS = LOAD 'input/stopwords.csv' USING PigStorage(',') AS (word:chararray);

BOOK = LOAD 'input/book.txt' USING PigStorage('\n') AS (line:chararray);

BOOK_WITH_NUM = RANK BOOK;

BOOK_LOWER = FOREACH BOOK_WITH_NUM GENERATE
    rank_BOOK AS line_num,
    LOWER(line) AS line;

BOOK_WORDS = FOREACH BOOK_LOWER GENERATE
    line_num,
    FLATTEN(TOKENIZE(line)) AS word;

CLEAN_WORDS = FOREACH BOOK_WORDS GENERATE
    line_num,
    REPLACE(LOWER(word), '[^a-z]', '') AS word;

FILTERED_WORDS = FILTER CLEAN_WORDS BY word IS NOT NULL AND word != '';

STOPWORDS_LOWER = FOREACH STOPWORDS GENERATE LOWER(word) AS word;

JOINED = JOIN FILTERED_WORDS BY word, STOPWORDS_LOWER BY word USING 'replicated';

GROUPED = GROUP JOINED BY FILTERED_WORDS::word;

RESULT = FOREACH GROUPED GENERATE
    group AS word,
    JOINED.FILTERED_WORDS::line_num AS lines_bag;

STORE RESULT INTO 'output/task1_2025-10-20' USING PigStorage('\t');
