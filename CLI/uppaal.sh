#!/bin/sh

if [ $(uname) = "Darwin" ]
then
if [ -z grep -o uppaal ]
then
container=$(docker run -d -p 2350:2350 uppaal-4.1.20)
if [ ! -z $1 ]
then
if [ $1 = "--start" ]
then
echo $container
exit
fi
fi
fi
fi

java -jar $UPPAALCLIPATH/cli.jar $*

if [ $(uname) = "Darwin" ]
then
if [ ! -z $container ]
then
docker stop $container &> /dev/null &
fi
fi