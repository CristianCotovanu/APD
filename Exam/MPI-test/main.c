#include <mpi.h>
#include<stdio.h>
#include <math.h>
#include <string.h>
#include <stdlib.h>

#define N 3
#define B 1
#define F 1
#define S 0

#define STEPS 2

int main (int argc, char *argv[])
{
    int num_tasks, rank;
    int provided;
    MPI_Init_thread(&argc, &argv, MPI_THREAD_MULTIPLE, &provided);

    MPI_Comm_size(MPI_COMM_WORLD, &num_tasks);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Status status;

    if (rank == S) {
		int* f_ranks = calloc(F, sizeof(int));
		for (int f = 0; f < F; f++) {
			f_ranks[f] = f + B + 1;
		}

		char* question = calloc(4, sizeof(char));
		MPI_Recv(question, 4, MPI_INT, MPI_ANY_SOURCE, 0, MPI_COMM_WORLD, &status);
		
		printf("Question S: %s\n", question);

		int B_count = B;

		if (question == "askB") {
			MPI_Send(f_ranks, F, MPI_INT, status.MPI_SOURCE, 0, MPI_COMM_WORLD);
		} else if (question == "askF") {
			MPI_Send(&B_count, 1, MPI_INT, status.MPI_SOURCE, 0, MPI_COMM_WORLD);
		}
    } else if (rank > S && rank < S + B) {	// B here
		int* F_sent = calloc(F, sizeof(int)); // -1 = received NU, 1 = received DA

		int* F_idx = calloc(F, sizeof(int));
		MPI_Send("askB", 5, MPI_CHAR, S, 0, MPI_COMM_WORLD);		// ask ids of F
		MPI_Recv(&F_idx, 1, MPI_INT, S, 0, MPI_COMM_WORLD, &status);  // recv ids of F

		for (int i = 0; i < F; i++) {
			printf("%d ", F_idx[i]);
		}
		printf("\n");

		for (int step = 0; step < STEPS; step++) {
			int randomF = rand() % F;
			MPI_Send("F", 2, MPI_CHAR, F_idx[randomF], 0, MPI_COMM_WORLD);	//send friend request

			char* answer = calloc(2, sizeof(char));
			MPI_Recv(answer, 2, MPI_CHAR, F_idx[randomF], 0, MPI_COMM_WORLD, &status); 	// recv request answer

			printf("B:%d Answer from %d is: %s\n", rank, F_idx[randomF], answer);

			if (answer == "DA") {
				F_sent[randomF] = 1;
			} else {
				F_sent[randomF] = -1;
			}
		}
    } else { 	// F here
		int B_count;
		MPI_Send("askB", 5, MPI_CHAR, S, 0, MPI_COMM_WORLD);	// ask number of Bs
		MPI_Recv(&B_count, 1, MPI_INT, S, 0, MPI_COMM_WORLD, &status); // recv number of Bs
		printf("%d ", B_count);

		int* B_friends = calloc(B_count, sizeof(int)); // -1 = sent NU, 1 = sent DA

		char* friend_request = calloc(2, sizeof(char));
		MPI_Recv(friend_request, 2, MPI_CHAR, MPI_ANY_SOURCE, 0, MPI_COMM_WORLD, &status); 	// recv request answer

		int randomAnsIdx = rand() % 2;
		char randomAns[3];
		if (randomAnsIdx == 0) {
			strcpy(randomAns, "DA");
			B_friends[status.MPI_SOURCE - 1] = 1; 
		} else {
			strcpy(randomAns, "NU");
			B_friends[status.MPI_SOURCE - 1] = -1;
		}

		MPI_Send(randomAns, 2, MPI_CHAR, status.MPI_SOURCE, 0, MPI_COMM_WORLD);
	}

    MPI_Finalize();
    return 0;
}
