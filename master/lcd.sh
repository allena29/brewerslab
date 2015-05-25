

if [ -f "simulator" ]
then
	echo "$1 LCD Display (Simulator Mode)"
	who=`whoami`
	cmd="python"
	cmd2="screen"
else
	echo "$1 LCD Display"
	who=" root"
	cmd="sudo python"
	cmd2="sudo screen"
fi

kill `ps -ef | grep "$cmd pitmLCDisplay.py" | grep -v SCREEN | grep -v grep | sed -e "s/$who\s*//" -e 's/\s.*//'` 2>/dev/null

if [ "$1" = "kill" ]
then
 exit 
fi

$cmd2 -dmS lcd $cmd pitmLCDisplay.py
if [ "$1" = "" ]
then 
	sleep 1
	$cmd2 -r lcd
fi
	
