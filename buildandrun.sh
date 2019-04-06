#!/bin/bash
mvn clean install
python rectangle.py > pipe &
java -jar target/bsynth-1.0-SNAPSHOT-jar-with-dependencies.jar < pipe

