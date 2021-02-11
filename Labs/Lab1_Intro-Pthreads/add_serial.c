#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

#define X 100
#define NUM_THREADS 4
#define MIN(X, Y) (((X) < (Y)) ? (X) : (Y))

int* arr;
int array_size;

typedef struct dto {
    int start_idx;
    int end_idx;
} dto;

void *add_x_to_arr(void *arg) {
  	dto args = *(dto*) arg;

	for (int i = args.startIdx; i < args.endIdx; i++) {
        arr[i] += X;
	}  
	
  	pthread_exit(NULL);
}

void check_thread_creation(int res, int thread_nr) {
	if (res) {
	  		printf("Eroare la crearea thread-ului %d\n", thread_nr);
	  		exit(-1);
	}
}

void check_thread_exit(int res, int thread_nr) {
	if (res) {
	  		printf("Eroare la asteptarea thread-ului %d\n", thread_nr);
	  		exit(-1);
	}
}

void print_array() {
    for (int i = 0; i < array_size; i++) {
        printf("%d", arr[i]);
        if (i != array_size - 1) {
            printf(" ");
        } else {
            printf("\n");
        }
    }
}

void serial_addition() {
    for (int i = 0; i < array_size; i++) {
        arr[i] += X;
    }
}

void parallel_addition() {
    pthread_t threads[NUM_THREADS];
  	int r;
  	long id;
  	void *status;
  	dto arguments[NUM_THREADS];
    
    for (int t_id = 0; t_id < NUM_THREADS; t_id++) {
        int start = t_id * (double)array_size / NUM_THREADS;
        int end = MIN((t_id + 1) * (double)array_size / NUM_THREADS, array_size);

        dto arg;
        arg.startIdx = start;
        arg.endIdx = end;

        arguments[t_id] = arg;
		r = pthread_create(&threads[t_id], NULL, add_x_to_arr, &arguments[t_id]);
        check_thread_creation(r, t_id);
    }

    for (int t_id = 0; t_id < NUM_THREADS; t_id++) {
		r = pthread_join(threads[t_id], &status);
        check_thread_exit(r, t_id);
  	}
}

int main(int argc, char *argv[]) {
    if (argc < 2) {
        perror("Specificati dimensiunea array-ului\n");
        exit(-1);
    }

    array_size = atoi(argv[1]);

    arr = malloc(array_size * sizeof(int));
    for (int i = 0; i < array_size; i++) {
        arr[i] = i;
    }

    // print_array();

    // serial_addition();

    parallel_addition();

    // print_array();

  	pthread_exit(NULL);
}
