#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <math.h>

#define MIN(X, Y) ((X < Y) ? X : Y)

FILE *file;
char *in_filename;

int P;
int L;
int *alphabet;
int thread_reading_iters_max;

pthread_mutex_t mutex;

void get_args(int argc, char **argv)
{
	if (argc < 4)
	{
		printf("Numar insuficient de parametri: ./main P L file_name\n");
		exit(1);
	}

	P = atoi(argv[1]);
	L = atoi(argv[2]);
	in_filename = argv[3];
}

void init()
{
	alphabet = calloc(26, sizeof(int));	// 26 letters in alphabet

	if (alphabet == NULL)
	{
		printf("Eroare la malloc!");
		exit(1);
	}
}

void print()
{
	printf("alphabet:\n");
	for (int i = 0; i < 26; i++) {
		printf("%c: %d\n", (char)i + 'a', alphabet[i]);
	}
}

void *thread_function(void *arg)
{
	int tid = *(int *)arg;
	int res;

	for (int readIter = 0; readIter < thread_reading_iters_max; readIter++) {
		for (int idx = 0; idx < L; idx++) {
			res = pthread_mutex_lock(&mutex);
			if (res) {
				printf("Mutex lock error\n");
			}

			char c = fgetc(file);
			
			if (c == EOF)
				pthread_exit(NULL);

			if (c != '\n') {
				++alphabet[c - 'a'];
				printf("read: %c from thread %d\n", c, tid);			// debugging purpose
			}

			res = pthread_mutex_unlock(&mutex);
			if (res) {
				printf("Mutex unlock error\n");
			}
		}
	}

	pthread_exit(NULL);
}

int compute_chars_in_file(char *in_filename)
{
	FILE* temp_file = fopen(in_filename, "r");
	if (temp_file == NULL)
	{
		printf("Eroare la deschiderea fisierului de intrare!\n");
		exit(1);
	}

	int char_count = 0;
	char ch;
	while((ch = fgetc(temp_file)) != EOF) {
		char_count++;
	}

	fclose(temp_file);
	return char_count;
}

int main(int argc, char *argv[])
{
	get_args(argc, argv);

	int res;
	int thread_args[P];
	pthread_t threads[P];

	int chars_in_file = compute_chars_in_file(in_filename);
	printf("Chars in file: %d\n", chars_in_file);
	
	thread_reading_iters_max = chars_in_file / P / L + 1;
	printf("Iterations for each thread to read %d\n", thread_reading_iters_max);

	init();

	pthread_mutex_init(&mutex, NULL);

	file = fopen(in_filename, "r");
	if (file == NULL)
	{
		printf("Eroare la deschiderea fisierului de intrare!\n");
		exit(1);
	}

	// se creeaza thread-urile
	for (int tid = 0; tid < P; tid++)
	{
		thread_args[tid] = tid;
		res = pthread_create(&threads[tid], NULL, thread_function, &thread_args[tid]);

		if (res) {
			printf("Eroare la crearea thread-ului %d\n", tid);
			exit(-1);
		}
	}
	
	// se asteapta thread-urile
	for (int tid = 0; tid < P; tid++)
	{
		res = pthread_join(threads[tid], NULL);
		if (res) {
			printf("Eroare la asteptarea thread-ului %d\n", tid);
			exit(-1);
		}
	}
	
	print();
	pthread_mutex_destroy(&mutex);
	free(alphabet);
	fclose(file);

	return 0;
}
