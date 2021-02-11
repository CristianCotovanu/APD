#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>

#define ROOT 0

int main (int argc, char *argv[])
{
    int  numtasks, rank;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
    MPI_Comm_rank(MPI_COMM_WORLD,&rank);

    int recv_num;
    MPI_Status status;

    // First process starts the circle.
    if (rank == ROOT) {
        // First process starts the circle.
        // Generate a random number.
        // Send the number to the next process.
        int number_to_send = rand() % 100;
        MPI_Send(&number_to_send, 1, MPI_INT, 1, 0, MPI_COMM_WORLD);
        MPI_Recv(&recv_num, 1, MPI_INT, numtasks - 1, 0, MPI_COMM_WORLD, &status);
        printf("Process with rank [%d], received number %d.\n", rank, recv_num);
    } else if (rank == numtasks - 1) {
        // Last process close the circle.
        // Receives the number from the previous process.
        // Increments the number.
        // Sends the number to the first process.
        MPI_Recv(&recv_num, 1, MPI_INT, rank - 1, 0, MPI_COMM_WORLD, &status);
        printf("Process with rank [%d], received number %d.\n", rank, recv_num);
        recv_num += 2;
        MPI_Send(&recv_num, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
    } else {
        // Middle process.
        // Receives the number from the previous process.
        // Increments the number.
        // Sends the number to the next process.
        MPI_Recv(&recv_num, 1, MPI_INT, rank - 1, 0, MPI_COMM_WORLD, &status);
        printf("Process with rank [%d], received number %d.\n", rank, recv_num);
        recv_num += 2;
        MPI_Send(&recv_num, 1, MPI_INT, rank + 1, 0, MPI_COMM_WORLD);
    }

    MPI_Finalize();

}

