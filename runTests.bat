:: A simple script to save typing
@echo off
javac -cp junit-4.12.jar account/*.java
java -cp junit-4.12.jar;hamcrest-core-1.3.jar;.  org.junit.runner.JUnitCore account.MainTest
