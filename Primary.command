#!/bin/bash
@echo off
REM chmod u+x /path/to/file.command
echo "HI"
set /p input="Enter Commit Text: "
echo %input%
touch ttt.txt
@echo on

day=$(date "+%d")
echo "day is $day"

printf "What is your name?  -> "

read NAME
NUMBER1=$NAME
date "+%Y-%m-%d %H:%M:%S"
echo "Hello, $NUMBER1.  Nice to meet you."

Store command in variable and run
VM="name of my VM"
CMD="ifconfig"
$CMD



For bourne shell:
sh myscript.sh
For bash:
bash myscript.sh




private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
}