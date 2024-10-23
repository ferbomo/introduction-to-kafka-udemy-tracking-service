# introduction-to-kafka-udemy-tracking-service

## How to run in local

## Run Kafka cluster
In one terminal tab, go to your kafka installation folder. For ex:
`cd /Users/john.doe/tools/kafka/kafka_2.12-3.7.0`

### Generate cluster id
Type this to generate the id: 
`KAFKA_CLUSTER_ID="$(bin/kafka-storage.sh random-uuid)"`

Check that the id has been created typing:
`echo $KAFKA_CLUSTER_ID`

And you'll get something like:
`qdJMY0YWRgGXTLbVSHF77w`

### Generate log storage location
Type this in the terminal to generate log storage location:
`bin/kafka-storage.sh format -t $KAFKA_CLUSTER_ID -c config/kraft/server.properties`

### Run server
`bin/kafka-server-start.sh config/kraft/server.properties`

### Stop server
CTRL+C

## Run the app
Open one terminal tab and type: 
`mvn spring-boot:run`

## Run the consumer
Open another terminal tab and go to your kafka installation folder:
`cd /Users/john.doe/tools/kafka/kafka_2.12-3.7.0`

And type:
`bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic dispatch.tracking  --property print.key=true --property key.separator=:`



