#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#define TWO_THREADS 2
#define NUM_THREADS 4
#define ITERATIONS 5

void *f(void *arg) {
  	long id = *(long*) arg;
  	printf("Hello World din thread-ul %ld!\n", id);
  	pthread_exit(NULL);
}

void *f_iterative(void *arg) {
  	long id = *(long*) arg;

	for (int i = 0; i < ITERATIONS; i++) {
  		printf("%d. Hello World din thread-ul %ld!\n", i, id);
	}  
	
  	pthread_exit(NULL);
}

void *f1(void *arg) {
  	long id = *(long*) arg;
	printf("Foo 1 - thread %ld\n", id);  	
	pthread_exit(NULL);
}

void *f2(void *arg) {
  	long id = *(long*) arg;
  	printf("Foo 2 - thread %ld\n", id);
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

void run_diff_funcs_on_threads() {
	pthread_t thread1;
	pthread_t thread2;

	long arg1 = 1;
	long arg2 = 2;
	
	int res1 = pthread_create(&thread1, NULL, f1, &arg1);
	check_thread_creation(res1, 1);
	
	int res2 = pthread_create(&thread2, NULL, f2, &arg2);
	check_thread_creation(res2, 2);

  	void *status1;
	res1 = pthread_join(thread1, &status1);
	check_thread_exit(res1, 1);

	void *status2;
	res2 = pthread_join(thread2, &status2);
	check_thread_exit(res2, 2);
}

int main(int argc, char *argv[]) {
	pthread_t threads[NUM_THREADS];
  	int r;
  	long id;
  	void *status;
  	long arguments[NUM_THREADS];
	long cores = sysconf(_SC_NPROCESSORS_CONF);

	run_diff_funcs_on_threads();

  	for (id = 0; id < NUM_THREADS; id++) {
  		arguments[id] = id;
		r = pthread_create(&threads[id], NULL, f, &arguments[id]);

		if (r) {
	  		printf("Eroare la crearea thread-ului %ld\n", id);
	  		exit(-1);
		}
  	}

  	for (id = 0; id < NUM_THREADS; id++) {
		r = pthread_join(threads[id], &status);

		if (r) {
	  		printf("Eroare la asteptarea thread-ului %ld\n", id);
	  		exit(-1);
		}
  	}

  	pthread_exit(NULL);
}
