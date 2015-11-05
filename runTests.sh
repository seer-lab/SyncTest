#!/bin/bash

#build all source and tests
javac -cp junit-4.12.jar account/*.java account/tests/*.java

#remove previous output files
rm account/out/*.txt

TESTS="Test10 Test100 Test1K Test10K Test100K"
# TESTS="Test10 Test100"

for t in $TESTS
do
    echo -n "Running $t... "
    for i in $(seq 1 $1)
    do
        java -cp junit-4.12.jar:hamcrest-core-1.3.jar:. org.junit.runner.JUnitCore account.tests.$t >> account/out/$t.txt
    done
    echo "Done!"
    notify-send --urgency=critical "$t has finished."
done

echo "\n----STATS----\n"

javac Parser.java
for t in $TESTS
do
    java Parser account/out/$t.txt $i
done

notify-send --urgency=critical "runTests.sh has finished!"
