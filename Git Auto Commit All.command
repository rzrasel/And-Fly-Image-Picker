#!/bin/bash
printf "Enter Commit Text: "
read input

dateTime=$(date "+%Y-%m-%d %H:%M:%S")
commit="git commit -m"
gitcommit="$commit $input - $dateTime"
echo "$gitcommit"
git add .
$gitcommit
git pull
git push --all
echo Process Complete Press Enter...