package org.wso2.carbon.ml.model.spark.transformations;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.testng.annotations.Test;
import org.wso2.carbon.ml.model.spark.transformations.LineToTokens;
import org.wso2.carbon.ml.model.spark.transformations.TokensToLabeledPoints;

import java.util.regex.Pattern;

public class TokensToLabeledPointsTest {

    @Test
    public void testCall() throws Exception {
        SparkConf conf = new SparkConf().setAppName("testLineToTokens").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> lines = sc.textFile("src/test/resources/pIndiansDiabetes.csv");
        Pattern pattern = Pattern.compile(",");
        LineToTokens lineToTokens = new LineToTokens(pattern);
        JavaRDD<String[]> tokens = lines.map(lineToTokens);
        TokensToLabeledPoints tokensToLabeledPoints = new TokensToLabeledPoints(8);
        JavaRDD<LabeledPoint> labeledPoints = tokens.map(tokensToLabeledPoints);
        sc.stop();
    }
}