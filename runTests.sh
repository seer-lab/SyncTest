#!/bin/bash

if [ "$#" -ne 4 ]; then
    echo "Usage: sh runTests.sh path/to/src path/to/tests path/to/outputs loopCount"
    echo "Important: Do not end paths with '/' !!"
    exit
fi

PATH_TO_SRC=$1
PATH_TO_TST=$2
PATH_TO_OUT=$3
LOOP_COUNT=$4


echo "-----------VARS-----------"
echo "PATH_TO_SRC: $PATH_TO_SRC"
echo "PATH_TO_TST: $PATH_TO_TST"
echo "PATH_TO_OUT: $PATH_TO_OUT"
echo "LOOP_COUNT: $LOOP_COUNT"
echo "--------------------------"

# build all source and tests
javac -cp junit-4.12.jar $PATH_TO_SRC/*.java $PATH_TO_TST/*.java Parser.java

# convert format from account/tests to account.test
RUN_TEST=$(echo "$PATH_TO_TST" | tr / .)

# Remove path and extension from tests
TESTS=$(ls $PATH_TO_TST/*.java | xargs -n 1 basename)
# echo "TESTS: $TESTS"
TESTS=$(echo "$TESTS" | cut -f 1 -d '.' )
# echo "TESTS: $TESTS"

for t in $TESTS
do
    echo "Running $t... "
    for i in $(seq 1 $LOOP_COUNT)
    do
        echo -n "Execution $i: "

        # start deadlock detection script
        csh checkDeadlock.sh $t $PATH_TO_OUT/$t-$i.txt &

        # run test and append output to text file
        java -cp junit-4.12.jar:hamcrest-core-1.3.jar:. org.junit.runner.JUnitCore $RUN_TEST.$t > $PATH_TO_OUT/$t-$i.txt

        # print status of each test
        if (grep --quiet OK $PATH_TO_OUT/$t-$i.txt) then
            echo "Passed"
        elif (grep --quiet deadlock $PATH_TO_OUT/$t-$i.txt) then
            echo -n "" # pkill will print "Killed" so we do nothing here
        elif (grep --quiet Failures $PATH_TO_OUT/$t-$i.txt) then
            echo "Failed"
        else
            echo "I don't know what happened"
        fi

        pkill -KILL -f checkDeadlock.sh
    done
done

# remove the old files so we don't confuse the parser
rm $PATH_TO_OUT/*-all.txt

# Combine all text files into one for the parser
for t in $TESTS
do
    for i in $(seq 1 $i)
    do
        cat $PATH_TO_OUT/$t-$i.txt >> $PATH_TO_OUT/$t-all.txt
    done
done

for t in $TESTS
do
    echo "\n======$t======"
    #parses the output and prints info to the terminal
    java Parser $PATH_TO_OUT/$t-all.txt $i
done
