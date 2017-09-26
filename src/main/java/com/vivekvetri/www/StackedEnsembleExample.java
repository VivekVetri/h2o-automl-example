package com.vivekvetri.www;

import hex.StackedEnsembleModel.StackedEnsembleParameters;
import hex.deeplearning.DeepLearningModel;
import hex.deeplearning.DeepLearningModel.DeepLearningParameters;
import hex.ensemble.StackedEnsemble;
import hex.deeplearning.DeepLearning;
import hex.tree.gbm.GBM;
import hex.tree.gbm.GBMModel;
import hex.tree.gbm.GBMModel.GBMParameters;
import hex.StackedEnsembleModel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.spark.sql.SparkSession;
import water.Key;
import water.fvec.H2OFrame;
import java.io.File;

import org.apache.spark.h2o.H2OContext;

/**
 * Created by vivek on 9/26/17.
 */

public class StackedEnsembleExample {

  public static void main(String[] args) throws FileNotFoundException {

    SparkSession spark = SparkSession
        .builder()
        .appName("H2O StackedEnsemble Example")
        .getOrCreate();

    H2OContext h2OContext = H2OContext.getOrCreate(spark);

    H2OFrame prostateData = new H2OFrame(new File("data/prostate.csv"));

    DeepLearningParameters dlParams = new DeepLearningParameters();
    dlParams._epochs = 100;
    dlParams._train = prostateData._key;
    dlParams._response_column = "CAPSULE";
    dlParams._variable_importances = true;
    dlParams._nfolds = 5;
    dlParams._seed = 1111;
    dlParams._keep_cross_validation_predictions = true;

    DeepLearning dl = new DeepLearning(dlParams);
    DeepLearningModel dlModel = dl.trainModel().get();
    System.out.println("Finished training deep learning model");

    GBMParameters gbmParams = new GBMParameters();
    gbmParams._train = prostateData._key;
    gbmParams._response_column = "CAPSULE";
    gbmParams._nfolds = 5;
    gbmParams._seed = 1111;
    gbmParams._keep_cross_validation_predictions = true;

    GBM gbm = new GBM(gbmParams);
    GBMModel gbmModel = gbm.trainModel().get();
    System.out.println("Finished training GBM model");

    StackedEnsembleParameters stackedEnsembleParameters = new StackedEnsembleModel.StackedEnsembleParameters();
    Key baseModelsKeyArray[] = new Key[2];
    baseModelsKeyArray[0] = gbmModel._key;
    baseModelsKeyArray[1] = dlModel._key;
    stackedEnsembleParameters._base_models = baseModelsKeyArray;
    stackedEnsembleParameters._train = prostateData._key;
    stackedEnsembleParameters._response_column = "CAPSULE";

    StackedEnsemble stackedEnsembleJob = new StackedEnsemble(stackedEnsembleParameters);

    StackedEnsembleModel stackedEnsembleModel = stackedEnsembleJob.trainModel().get();

    System.out.println("Finished training Stacked Ensemble model");

    // MOJO not supported for Stacked ensemble yet !
    // String modelSavePath = "data/";
    // stackedEnsembleModel.getMojo().writeTo(new FileOutputStream(new File(modelSavePath + "/" + stackedEnsembleModel._key + ".zip")));

    h2OContext.stop(true);
  }
}
