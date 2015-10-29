build:
	javac -cp junit-4.12.jar account/*.java

run:
	java account.Command out.txt 2

clean:
	rm account/*.class

test:
	java -cp junit-4.12.jar:hamcrest-core-1.3.jar:.  org.junit.runner.JUnitCore account.MainTest || true

multi-test:
	sh runTests.sh 50 | grep -e Failures -e OK
