#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <math.h>

#define MIN(X, Y) ((X < Y) ? X : Y)

int L; // latura matricei
int N; // numarul de elemente de sortat
int P;
int *v;
int *vQSort;
int **M;

pthread_barrier_t barrier;

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

void display_matrix(int **M)
{
	int i, j;
	int display_width = 2 + log10(N);

	for (i = 0; i < L; i++)
	{
		for (j = 0; j < L; j++)
		{
			printf("%*i", display_width, M[i][j]);
		}
		printf("\n");
	}
}

void copy_matrix_in_vector(int *v, int **M)
{
	int i, j;
	for (i = 0; i < L; i++)
	{
		if (i % 2 == 0)
		{
			for (j = 0; j < L; j++)
			{
				v[i * L + j] = M[i][j];
			}
		}
		else
		{
			for (j = L; j > 0; j--)
			{
				v[i * L + (L - j)] = M[i][j - 1];
			}
		}
	}
}

int cmp(const void *a, const void *b)
{
	int A = *(int *)a;
	int B = *(int *)b;
	return A - B;
}

int cmpdesc(const void *a, const void *b)
{
	int A = *(int *)a;
	int B = *(int *)b;
	return B - A;
}

void get_args(int argc, char **argv)
{
	if (argc < 3)
	{
		printf("Numar insuficient de parametri: ./shear L P (L = latura matricei)\n");
		exit(1);
	}

	L = atoi(argv[1]);
	N = L * L;
	P = atoi(argv[2]);
}

void init()
{
	int i, j;
	v = malloc(sizeof(int) * N);
	vQSort = malloc(sizeof(int) * N);
	M = malloc(sizeof(int *) * L);

	if (v == NULL || vQSort == NULL || M == NULL)
	{
		printf("Eroare la malloc!");
		exit(1);
	}

	for (i = 0; i < L; i++)
	{
		M[i] = malloc(sizeof(int) * L);
	}

	srand(42);

	for (i = 0; i < L; i++)
	{
		for (j = 0; j < L; j++)
		{
			M[i][j] = rand() % N;
		}
	}
}

void print()
{
	printf("M:\n");
	display_matrix(M);

	copy_matrix_in_vector(v, M);
	printf("v:\n");
	display_vector(v);
	printf("vQSort:\n");
	display_vector(vQSort);
	compare_vectors(v, vQSort);
}

void free_matrix()
{
	for (int i = 0; i < L; i++)
	{
		free(M[i]);
	}
	free(M);
}

void *thread_function(void *arg)
{
	int thread_id = *(int *)arg;
	int aux[L];
	int start = thread_id * (double)L / P;
	int end = MIN((thread_id + 1) * (double)L / P, L);

	for (int i = start; i < end; i++) {
		if (i % 2)
		{
			qsort(M[i], L, sizeof(int), cmpdesc);
		}
		else
		{
			qsort(M[i], L, sizeof(int), cmp);
		}
	}
	
	pthread_barrier_wait(&barrier);

	for (int i = start; i < end; i++)
	{
		for (int j = 0; j < L; j++)
		{
			aux[j] = M[j][i];
		}

		qsort(aux, L, sizeof(int), cmp);

		for (int j = 0; j < L; j++)
		{
			M[j][i] = aux[j];
		}
	}

	pthread_barrier_wait(&barrier);

	pthread_exit(NULL);
}

void shear_sort_seq()
{
	int i, j, k, aux[L];

	for (k = 0; k < log(N) + 1; k++)
	{
		// se sorteaza liniile pare crescator
		// se sorteaza liniile impare descrescator
		for (i = 0; i < L; i++)
		{
			if (i % 2)
			{
				qsort(M[i], L, sizeof(int), cmpdesc);
			}
			else
			{
				qsort(M[i], L, sizeof(int), cmp);
			}
		}

		// se sorteaza coloanele descrescator
		for (i = 0; i < L; i++)
		{
			for (j = 0; j < L; j++)
			{
				aux[j] = M[j][i];
			}

			qsort(aux, L, sizeof(int), cmp);

			for (j = 0; j < L; j++)
			{
				M[j][i] = aux[j];
			}
		}
	}
}

void shear_sort_parallel()
{
	pthread_t tid[P];
	int thread_id[P];
	pthread_barrier_init(&barrier, NULL, P);
	
	for (int k = 0; k < log(N) + 1; k++)
	{
		// se creeaza thread-urile
		for (int i = 0; i < P; i ++)
		{
			thread_id[i] = i;
			pthread_create(&tid[i], NULL, thread_function, &thread_id[i]);
		}
		
		// se asteapta thread-urile
		for (int i = 0; i < P; i++)
		{
			pthread_join(tid[i], NULL);
		}
	}

	pthread_barrier_destroy(&barrier);
}

int main(int argc, char *argv[])
{
	get_args(argc, argv);
	init();

	// se sorteaza etalonul
	copy_matrix_in_vector(vQSort, M);
	qsort(vQSort, N, sizeof(int), cmp);

	// shear_sort_seq();
	shear_sort_parallel();

	// se afiseaza matricea
	// se afiseaza vectorul etalon
	// se afiseaza matricea curenta sub forma de vector
	// se compara cele doua
	print();

	free(v);
	free(vQSort);
	free_matrix();

	return 0;
}
