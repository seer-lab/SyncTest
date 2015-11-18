#!/bin/bash

# build all source and tests
javac -cp junit-4.12.jar account/*.java account/tests/*.java Parser.java

TESTS=$(zenity --height=250 --list --checklist \
        --title='Synctest' --text="Select the number of accounts to test with" \
         --column=Boxes --column=Selections \
        TRUE Test2 TRUE Test10 TRUE Test100 FALSE Test1K FALSE Test10K FALSE Test100K --separator=' ')

LOOP=$(zenity --entry \
        --title="Synctest" \
        --text="Enter the amount of times to run each test:" \
        --entry-text "10")

(for t in $TESTS
do
    echo "# Running $t..."
    for i in $(seq 1 $LOOP)
    do
        echo "$i/$LOOP*100" | bc -l

        # start deadlock detection script
        csh checkDeadlock.sh $t account/out/$t/$t-$i.txt &

        # run test and redirect output to text file
        java -cp junit-4.12.jar:hamcrest-core-1.3.jar:. org.junit.runner.JUnitCore account.tests.$t > account/out/$t/$t-$i.txt

        pkill -KILL -f checkDeadlock.sh
    done
done
echo "# Done!") | zenity --progress \
        --title="Synctest" \
        --percentage=0

PASS=0
FAIL=0
DEAD=0

# Combine all text files into one for the parser
for t in $TESTS
do
    for i in $(seq 1 $LOOP)
    do
        if (grep --quiet OK account/out/$t/$t-$i.txt) then
            PASS=$(($PASS+1))
        elif (grep --quiet deadlock account/out/$t/$t-$i.txt) then
            DEAD=$(($DEAD+1))
        else
            FAIL=$(($FAIL+1))
        fi
    done

    zenity --list \
          --title="Synctest Results - $t" \
          --width=400 --height=300 \
          --column="Passed" --column="Failed" --column="Deadlocked" --column="Total" \
            $PASS $FAIL $DEAD $LOOP

    PASS=0
    FAIL=0
    DEAD=0
done
