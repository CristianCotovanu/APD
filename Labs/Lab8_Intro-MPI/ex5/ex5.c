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

    // Checks the number of processes allowed.
    if (numtasks != 2) {
        printf("Wrong number of processes. Only 2 allowed!\n");
        MPI_Finalize();
        return 0;
    }

    // How many numbers will be sent.
    int send_numbers = 10;

    if (rank == ROOT) {
        // Generate the random numbers.
        // Generate the random tags.
        // Sends the numbers with the tags to the second process.
        int random_num = 0;
        int random_tag = 0;

        for (int i = 0; i < send_numbers; i++) {
            random_num = rand() % 100;
            random_tag = rand() % 25;

            MPI_Send(&random_num, 1, MPI_INT, 1, random_tag, MPI_COMM_WORLD);
            printf("Sent %d with tag %d\n", rank, random_num, random_tag);
        }
    } else {
        // Receives the information from the first process.
        // Prints the numbers with their corresponding tags.
        MPI_Status status;
        int recv_num = 0;

        for (int i = 0; i < send_numbers; i++) {
            MPI_Recv(&recv_num, 1, MPI_INT, 0, MPI_ANY_TAG, MPI_COMM_WORLD, &status);
            printf("Received %d with tag %d\n", rank, recv_num, status.MPI_TAG);
        }
    }

    MPI_Finalize();

}

