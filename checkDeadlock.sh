#!/bin/csh
# Auto-generated mutant generation script

# echo "-------------------------"
# echo "Deadlock Detection Script"
# echo "-------------------------"

set GREPFILE = "$2.grep"

#First set upper bound to terminate this process
set x = 0
while ($x == 0)
	# echo "Waiting .6 seconds..."
	sleep 2

	# echo -n "Checking for deadlock..."

	#pgrep -f $1

	pkill -QUIT -f java.+$1

	grep deadlock $2 > $GREPFILE

	if (! -z $GREPFILE) then
		# echo " Deadlock detected in $1!"
		pkill -KILL -f java.+$1
		exit 1
	# else
	# 	echo " None found!"
	# 	exit 1
	endif
end
