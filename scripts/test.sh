if [ -z "$1" ]; then
	echo "Assignment name required"
	exit 1
fi

ASSIGNMENT=$1

if [ -n "$2" ]; then
  CHECKPOINT="-$2"
fi

if ! [ -d assignments/$ASSIGNMENT$CHECKPOINT ] ; then
	echo "Assignment not found: $ASSIGNMENT$CHECKPOINT"
	exit 1
fi

set -xe
cd assignments/$ASSIGNMENT$CHECKPOINT

javac -cp ".:../../target/runtime-dependencies/*" $ASSIGNMENT.java ${ASSIGNMENT}Test.java

java -jar ../../target/runtime-dependencies/junit-platform-console-standalone-1.7.0.jar -cp .:../../target/runtime-dependencies/* -c ${1}Test

