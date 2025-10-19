package com.example;

import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender;

import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.common.TasteException;

import java.io.File;
import java.util.List;

public class MahoutRecommendationExample {

    private static DataModel loadModel(String filePath) throws Exception {
        return new FileDataModel(new File(filePath));
    }

    public static List<?> recommendItemBased(int userId, int number, DataModel model) throws TasteException {
        ItemSimilarity similarity = new TanimotoCoefficientSimilarity(model);
        Recommender recommender = new GenericItemBasedRecommender(model, similarity);
        return recommender.recommend(userId, number);
    }

    public static List<?> recommendSlopeOne(int userId, int number, DataModel model) throws TasteException {
        Recommender recommender = new SlopeOneRecommender(model);
        return recommender.recommend(userId, number);
    }

    public static double evaluate(RecommenderBuilder builder, DataModel model, int runs) throws TasteException {
        RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
        double sum = 0.0;
        for (int i = 0; i < runs; i++) {
            sum += evaluator.evaluate(builder, null, model, 0.7, 1.0);
        }
        return sum / runs;
    }

    public static void main(String[] args) throws Exception {
        DataModel model = loadModel("ratings.csv");

        // Item-based builder
        RecommenderBuilder itemBuilder = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel model) throws TasteException {
                ItemSimilarity similarity = new TanimotoCoefficientSimilarity(model);
                return new GenericItemBasedRecommender(model, similarity);
            }
        };

        // SlopeOne builder
        RecommenderBuilder slopeBuilder = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel model) throws TasteException {
                return new SlopeOneRecommender(model);
            }
        };

        int runs = 10;
        double itemScore = evaluate(itemBuilder, model, runs);
        double slopeScore = evaluate(slopeBuilder, model, runs);

        System.out.println("Среднее MAE ItemBased (Tanimoto): " + itemScore);
        System.out.println("Среднее MAE SlopeOne: " + slopeScore);

        int testUser = 1;
        int recCount = 5;
        System.out.println("Item-based рекомендации: " + recommendItemBased(testUser, recCount, model));
        System.out.println("SlopeOne рекомендации: " + recommendSlopeOne(testUser, recCount, model));
    }
}
