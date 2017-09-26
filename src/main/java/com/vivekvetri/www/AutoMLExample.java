package com.vivekvetri.www;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;

import ai.h2o.automl.AutoML;
import ai.h2o.automl.AutoMLBuildSpec;

import org.apache.spark.h2o.H2OContext;
import org.apache.spark.h2o.H2OContext.implicits$;

import org.apache.spark.sql.SparkSession;
import water.*;
import water.fvec.H2OFrame;

/**
 * Created by vivek on 9/26/17.
 */

public class AutoMLExample {

  public static void main(String[] args) throws FileNotFoundException {

    SparkSession spark = SparkSession
        .builder()
        .appName("H2O AutoML Example")
        .getOrCreate();

    H2OContext h2OContext = H2OContext.getOrCreate(spark);

    AutoMLBuildSpec autoMLBuildSpec = new AutoMLBuildSpec();

    H2OFrame trainFrame = new H2OFrame(new File("data/prostate.csv"));
    H2OFrame validationFrame = new H2OFrame(new File("data/prostate.csv"));

    autoMLBuildSpec.input_spec.training_frame = trainFrame._key;
    autoMLBuildSpec.input_spec.validation_frame = validationFrame._key;

    autoMLBuildSpec.input_spec.response_column = "CAPSULE";
    autoMLBuildSpec.build_control.loss = "AUTO";
    autoMLBuildSpec.build_control.stopping_criteria.set_max_runtime_secs(30);

    AutoML aml = AutoML.makeAutoML(Key.make(), new Date(), autoMLBuildSpec);
    AutoML.startAutoML(autoMLBuildSpec).get();

    //save the leader model
    String modelSavePath = "data/";
    aml.leader().getMojo().writeTo(new FileOutputStream(new File(modelSavePath + "/" + aml.leader()._key + ".zip")));

    //print the leaderboard
    System.out.println(aml.leaderboard());

    //details of the leader model
    System.out.println(aml.leader());

    //stop h2oContext and sparkContext
    h2OContext.stop(true);
  }
}
