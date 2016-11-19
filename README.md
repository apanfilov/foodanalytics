# foodanalytics

Playing with Spark 2.0.1

To build :
mvn clean package assembly:assembly

To run :
spark-submit --master local --class com.apanfilov.testtask.DriverClass [path_to_jar]/FoodAnalytics-1.0-SNAPSHOT-jar-with-dependencies.jar --pathToData [path_to_data]/Reviews.csv [--translate]
