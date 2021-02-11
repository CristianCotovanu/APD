#!/bin/bash

N=100
P=2

if [ ! -f "multiply_seq" ]
then
    echo "Nu exista binarul multiply_seq"
    exit
fi

if [ ! -f "multiply" ]
then
    echo "Nu exista binarul multiply"
    exit
fi

./multiply_seq $N > seq.txt
./multiply $N $P > par.txt
# ./strassen $N > strassen.txt

diff seq.txt par.txt
# diff seq.txt strassen.txt

rm -rf seq.txt par.txt strassen.txt