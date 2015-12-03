#!/bin/bash


PATH_TO_SRC=$(zenity --entry \
        --title="Synctest" \
        --text="Enter the path to the source files:" \
        --entry-text "/path/to/src")
PATH_TO_TST=$(zenity --entry \
        --title="Synctest" \
        --text="Enter the path to the test files:" \
        --entry-text "/path/to/tests")
PATH_TO_OUT=$(zenity --entry \
        --title="Synctest" \
        --text="Enter the path to the output files:" \
        --entry-text "/path/to/out")
LOOP_COUNT=$(zenity --entry \
        --title="Synctest" \
        --text="Enter the amound of times to run each test:" \
        --entry-text "10")

# build all source and tests
javac -cp junit-4.12.jar $PATH_TO_SRC/*.java $PATH_TO_TST/*.java Parser.java

# convert format from account/tests to account.test
RUN_TEST=$(echo "$PATH_TO_TST" | tr / .)

# Remove path and extension from tests
TESTS=$(ls $PATH_TO_TST/*.java | xargs -n 1 basename)
# echo "TESTS: $TESTS"
TESTS=$(echo "$TESTS" | cut -f 1 -d '.' )
# echo "TESTS: $TESTS"

(for t in $TESTS
do
    echo "# Running $t... "
    for i in $(seq 1 $LOOP_COUNT)
    do
        echo "$i/$LOOP_COUNT*100" | bc -l
        # start deadlock detection script
        csh checkDeadlock.sh $t $PATH_TO_OUT/$t-$i.txt &

        # run test and append output to text file
        java -cp junit-4.12.jar:hamcrest-core-1.3.jar:. org.junit.runner.JUnitCore $RUN_TEST.$t > $PATH_TO_OUT/$t-$i.txt

        pkill -KILL -f checkDeadlock.sh
    done
done
echo "# Done!") | zenity --progress \
        --title="Synctest" \
        --percentage=0


clear
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
    java Parser $PATH_TO_OUT/$t-all.txt $LOOP_COUNT
done



# PASS=0
# FAIL=0
# DEAD=0
#
# # Combine all text files into one for the parser
# for t in $TESTS
# do
#     for i in $(seq 1 $LOOP_COUNT)
#     do
#         if (grep --quiet OK $PATH_TO_OUT/$t-$i.txt) then
#             PASS=$(($PASS+1))
#         elif (grep --quiet deadlock $PATH_TO_OUT/$t-$i.txt) then
#             DEAD=$(($DEAD+1))
#         else
#             FAIL=$(($FAIL+1))
#         fi
#     done
#
#     zenity --list \
#           --title="Synctest Results - $t" \
#           --width=400 --height=300 \
#           --column="Passed" --column="Failed" --column="Deadlocked" --column="Total" \
#             $PASS $FAIL $DEAD $LOOP_COUNT
#
#     PASS=0
#     FAIL=0
#     DEAD=0
# done
