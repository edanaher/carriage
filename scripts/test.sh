if [ -z "$1" ]; then
	echo "Assignment name required"
	exit 1
fi

if ! [ -d assignments/$1 ] ; then
	echo "Assignment not found: $1"
	exit 1
fi

set -xe
cd assignments/$1

javac -cp ".:../../target/runtime-dependencies/*" $1.java ${1}Test.java

java -jar ../../target/runtime-dependencies/junit-platform-console-standalone-1.7.0.jar -cp .:../../target/runtime-dependencies/* -c ${1}Test

