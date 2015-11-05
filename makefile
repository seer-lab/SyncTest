build:
	javac -cp junit-4.12.jar account/*.java account/tests/*.java

run:
	java account.Command out.txt 2

clean:
	rm account/*.class account/tests/*.class Parser.class

test:
	time sh runTests.sh 50
