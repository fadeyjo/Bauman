#!/usr/bin/env python3
from pyspark.sql import SparkSession
from pyspark.sql import functions as F

spark = SparkSession.builder.appName("GenreAverageRating").getOrCreate()

ratings_path = "file:///home/hduser/spark_data/rating.csv"
movies_path  = "file:///home/hduser/spark_data/movies.csv"
output_path  = "file:///home/hduser/spark_data/output"

ratings = spark.read.option("header", True).option("inferSchema", True).option("sep", ";").csv(ratings_path)
movies  = spark.read.option("header", True).option("inferSchema", True).option("sep", ";").csv(movies_path)

ratings = ratings.select("movieId", "rating")
movies  = movies.select("movieId", "genres")

movies = movies.withColumn("genre", F.split(F.col("genres"), "\\|").getItem(0)).select("movieId", "genre")

joined = ratings.join(movies, on="movieId", how="inner")

#genre_avg = joined.groupBy("genre").agg(F.avg("rating").alias("av_rating_raw"))
genre_avg = joined.groupBy("genre").avg('rating')
genre_avg.show()
genre_avg = genre_avg.withColumn(
    "av_rating",
    (F.floor(F.col("av_rating_raw") * 10 + F.lit(0.5)) / F.lit(10))
)

result = genre_avg.select("genre", "av_rating").orderBy("genre")

result.show(truncate=False)

result.write.mode("overwrite").option("header", True).option("sep", ";").csv(output_path)

spark.stop()
