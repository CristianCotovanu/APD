build:
	mpic++ tema3.cpp -o main

run:
	mpirun -np 5 --oversubscribe main ./tests/in/input1.txt

test: build run clean

clean:
	rm main
