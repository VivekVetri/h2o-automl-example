###I) Overview :-

This project contains AutoML Example.

###II) Requirements :-

* Spark 2.1.x
* Scala 2.11.8
* Java 1.8
* H2O version 3.14.10.2
* Sparkling Water 2.1.15

###III) Steps :-

####1. Install the Sparling Water jar to the local maven repository

```
mvn install:install-file -Dfile=lib/sparkling-water-assembly_2.11-2.1.15-SNAPSHOT-all.jar -DgroupId=ai.h2o  -DartifactId=sparkling-water-assembly_2.11  -Dversion=2.1.15 -Dpackaging=jar
```
    
####2. Build with Maven

```
mvn package
```

####3. Run the example

```
spark-submit --master local[*] --class com.vivekvetri.www.AutoMLExample target/h2o-automl-example-1.0-jar-with-dependencies.jar

```
