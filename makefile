build:
	javac -cp junit-4.12.jar account/*.java account/tests/*.java Parser.java

run:
	java account.Command 2

clean:
	rm account/*.class account/tests/*.class Parser.class

purge:
	rm account/*.class account/tests/*.class Parser.class
	rm account/out/Test*/Test*.txt account/out/Test*/*.txt.grep

test:
	sh runTests.sh 50
