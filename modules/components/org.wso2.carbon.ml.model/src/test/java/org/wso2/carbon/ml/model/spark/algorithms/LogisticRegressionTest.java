package org.wso2.carbon.ml.model.spark.algorithms;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.testng.annotations.Test;
import org.wso2.carbon.ml.model.dto.LogisticRegressionModelSummary;
import org.wso2.carbon.ml.model.spark.transformations.Header;
import org.wso2.carbon.ml.model.spark.transformations.LineToTokens;
import org.wso2.carbon.ml.model.spark.transformations.TokensToLabeledPoints;

import java.util.regex.Pattern;

import static org.testng.Assert.*;

public class LogisticRegressionTest {

    @Test
    public void testGetModelSummary() throws Exception {
        SparkConf conf = new SparkConf().setAppName("testLineToTokens").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> lines = sc.textFile("src/test/resources/pIndiansDiabetes.csv");
        Pattern pattern = Pattern.compile(",");
        LineToTokens lineToTokens = new LineToTokens(pattern);
        String headerRow = lines.take(1).get(0);
        Header header = new Header(headerRow);
        JavaRDD<String> data = lines.filter(header);
        JavaRDD<String[]> tokens = data.map(lineToTokens);
        TokensToLabeledPoints tokensToLabeledPoints = new TokensToLabeledPoints(8);
        JavaRDD<LabeledPoint> labeledPoints = tokens.map(tokensToLabeledPoints);
        JavaRDD<LabeledPoint> trainingData = labeledPoints.sample(false, 0.7, 11L);
        JavaRDD<LabeledPoint> testingData = labeledPoints.subtract(trainingData);
        LogisticRegression logisticRegression = new LogisticRegression();
        LogisticRegressionModel model = logisticRegression.trainWithSGD(trainingData, 0.01,
                100,
                "L1", 0.001, 1.0);
        model.clearThreshold();
//        LogisticRegressionModelSummary modelSummary = logisticRegression.getModelSummary
//                (logisticRegression.test(model, testingData));
//        assertEquals(modelSummary.getAuc(), 0.54, 0.01);
        sc.stop();
    }
}