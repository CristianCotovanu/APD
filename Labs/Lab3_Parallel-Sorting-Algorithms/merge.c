#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <math.h>

#define MIN(X, Y) ((X < Y) ? X : Y)

int N;
int P;
int *v;
int *vQSort;
int *vNew;

pthread_barrier_t barrier;

typedef struct
{
	int tid;
	int width;
} Dto;

void merge(int *source, int start, int mid, int end, int *destination)
{
	int iA = start;
	int iB = mid;
	int i;

	for (i = start; i < end; i++)
	{
		if (end == iB || (iA < mid && source[iA] <= source[iB]))
		{
			destination[i] = source[iA];
			iA++;
		}
		else
		{
			destination[i] = source[iB];
			iB++;
		}
	}
}

void compare_vectors(int *a, int *b)
{
	int i;

	for (i = 0; i < N; i++)
	{
		if (a[i] != b[i])
		{
			printf("Sortare incorecta\n");
			return;
		}
	}

	printf("Sortare corecta\n");
}

void display_vector(int *v)
{
	int i;
	int display_width = 2 + log10(N);

	for (i = 0; i < N; i++)
	{
		printf("%*i", display_width, v[i]);
	}

	printf("\n");
}

int cmp(const void *a, const void *b)
{
	int A = *(int *)a;
	int B = *(int *)b;
	return A - B;
}

int is_power_of_two(int n)
{
	if (n == 0)
	{
		return 0;
	}

	return (ceil(log2(n)) == floor(log2(n)));
}

void get_args(int argc, char **argv)
{
	if (argc < 3)
	{
		printf("Numar insuficient de parametri: ./merge N P (N trebuie sa fie putere a lui 2)\n");
		exit(1);
	}

	N = atoi(argv[1]);
	if (!is_power_of_two(N))
	{
		printf("N trebuie sa fie putere a lui 2\n");
		exit(1);
	}

	P = atoi(argv[2]);
}

void init()
{
	int i;
	v = malloc(sizeof(int) * N);
	vQSort = malloc(sizeof(int) * N);
	vNew = malloc(sizeof(int) * N);

	if (v == NULL || vQSort == NULL || vNew == NULL)
	{
		printf("Eroare la malloc!");
		exit(1);
	}

	srand(42);

	for (i = 0; i < N; i++)
		v[i] = rand() % N;
}

void print()
{
	printf("v:\n");
	display_vector(v);
	printf("vQSort:\n");
	display_vector(vQSort);
	compare_vectors(v, vQSort);
}

void *thread_function(void *arg)
{
	Dto dto = *(Dto *)arg;

	// implementati aici merge sort paralel
	long start = dto.tid * dto.width * (double)N / P;

	for (long i = start; i < N ; i += 2 * dto.width)
	{
		merge(v, i, i + dto.width, i + 2 * dto.width, vNew);
	}

	int b_res = pthread_barrier_wait(&barrier);

	pthread_exit(NULL);
}

void merge_sort_seq()
{
	int *aux;
	for (int width = 1; width < N; width = 2 * width)
	{
		for (int i = 0; i < N; i = i + 2 * width)
		{
			merge(v, i, i + width, i + 2 * width, vNew);
		}

		aux = v;
		v = vNew;
		vNew = aux;
	}
}

void merge_sort_parallel()
{
	Dto thread_id[P];
	pthread_t tid[P];
	int *aux;

	pthread_barrier_init(&barrier, NULL, P);

	for (int width = 1; width < N; width = 2 * width)
	{
		// se creeaza thread-urile
		for (int i = 0; i < P; i++)
		{
			thread_id[i].tid = i;
			thread_id[i].width = width;
			pthread_create(&tid[i], NULL, thread_function, &thread_id[i]);
		}
		
		// se asteapta thread-urile
		for (int i = 0; i < P; i++)
		{
			pthread_join(tid[i], NULL);
		}

		aux = v;
		v = vNew;
		vNew = aux;
	}

	pthread_barrier_destroy(&barrier);
}

int main(int argc, char *argv[])
{
	get_args(argc, argv);
	init();

	// se sorteaza vectorul etalon
	for (int i = 0; i < N; i++)
		vQSort[i] = v[i];
	qsort(vQSort, N, sizeof(int), cmp);

	// merge_sort_seq();
	merge_sort_parallel();

	print();

	free(v);
	free(vQSort);
	free(vNew);

	return 0;
}
