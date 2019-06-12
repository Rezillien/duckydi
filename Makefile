
build: 
	mvn clean install
	mkfifo pipe

clean:
	mvn clean
	rm pipe

run:
	python rectangle.py > pipe &
	java -jar target/bsynth-1.0-SNAPSHOT-jar-with-dependencies.jar < pipe


