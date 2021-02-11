#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <pthread.h>

#define MIN(X, Y) ((X < Y) ? X : Y)

// structura pentru un numar complex
typedef struct _complex
{
	double a;
	double b;
} complex;

// structura pentru parametrii unei rulari
typedef struct _params
{
	int is_julia, iterations;
	double x_min, x_max, y_min, y_max, resolution;
	complex c_julia;
} params;

char *in_filename_julia;
char *in_filename_mandelbrot;
char *out_filename_julia;
char *out_filename_mandelbrot;

int P;
pthread_barrier_t barrier;

int height_julia;
int width_julia;
int **result_julia;
params *p_julia;

int height_mandel;
int width_mandel;
int **result_mandel;
params *p_mandel;

// citeste argumentele programului
void get_args(int argc, char **argv)
{
	if (argc < 6)
	{
		printf("Numar insuficient de parametri:\n\t"
			   "./tema1_par fisier_intrare_julia fisier_iesire_julia "
			   "fisier_intrare_mandelbrot fisier_iesire_mandelbrot P\n");
		exit(1);
	}

	in_filename_julia = argv[1];
	out_filename_julia = argv[2];
	in_filename_mandelbrot = argv[3];
	out_filename_mandelbrot = argv[4];
	P = atoi(argv[5]);
}

// citeste fisierul de intrare
void read_input_file(char *in_filename, params *par)
{
	FILE *file = fopen(in_filename, "r");
	if (file == NULL)
	{
		printf("Eroare la deschiderea fisierului de intrare!\n");
		exit(1);
	}

	fscanf(file, "%d", &par->is_julia);
	fscanf(file, "%lf %lf %lf %lf",
		   &par->x_min, &par->x_max, &par->y_min, &par->y_max);
	fscanf(file, "%lf", &par->resolution);
	fscanf(file, "%d", &par->iterations);

	if (par->is_julia)
	{
		fscanf(file, "%lf %lf", &par->c_julia.a, &par->c_julia.b);
	}

	fclose(file);
}

// scrie rezultatul in fisierul de iesire
void write_output_file(char *out_filename, int **result, int width, int height)
{
	int i, j;

	FILE *file = fopen(out_filename, "w");
	if (file == NULL)
	{
		printf("Eroare la deschiderea fisierului de iesire!\n");
		return;
	}

	fprintf(file, "P2\n%d %d\n255\n", width, height);
	for (i = 0; i < height; i++)
	{
		for (j = 0; j < width; j++)
		{
			fprintf(file, "%d ", result[i][j]);
		}
		fprintf(file, "\n");
	}

	fclose(file);
}

// aloca memorie pentru rezultat
int **allocate_memory(int width, int height)
{
	int **result;
	int i;

	result = malloc(height * sizeof(int *));
	if (result == NULL)
	{
		printf("Eroare la malloc!\n");
		exit(1);
	}

	for (i = 0; i < height; i++)
	{
		result[i] = malloc(width * sizeof(int));
		if (result[i] == NULL)
		{
			printf("Eroare la malloc!\n");
			exit(1);
		}
	}

	return result;
}

void read_and_init_julia() {
	p_julia = (params *)calloc(1, sizeof(params));
	read_input_file(in_filename_julia, p_julia);
	width_julia = (p_julia->x_max - p_julia->x_min) / p_julia->resolution;
	height_julia = (p_julia->y_max - p_julia->y_min) / p_julia->resolution;
	result_julia = allocate_memory(width_julia, height_julia);
}

void read_and_init_mandel() {
	p_mandel = (params *)calloc(1, sizeof(params));
	read_input_file(in_filename_mandelbrot, p_mandel);
	width_mandel = (p_mandel->x_max - p_mandel->x_min) / p_mandel->resolution;
	height_mandel = (p_mandel->y_max - p_mandel->y_min) / p_mandel->resolution;
	result_mandel = allocate_memory(width_mandel, height_mandel);
}

// elibereaza memoria alocata
void free_memory(int **result, int height)
{
	int i;

	for (i = 0; i < height; i++)
	{
		free(result[i]);
	}
	free(result);
}

void free_resources() {
	pthread_barrier_destroy(&barrier);
	free_memory(result_julia, height_julia);
	free_memory(result_mandel, height_mandel);
}

void run_julia(int start, int end) {
	int h, w;
	for (w = 0; w < width_julia; w++)
	{
		for (h = start; h < end; h++)
		{
			int step = 0;
			complex z = {.a = w * p_julia->resolution + p_julia->x_min,
						 .b = h * p_julia->resolution + p_julia->y_min};

			while (sqrt(pow(z.a, 2.0) + pow(z.b, 2.0)) < 2.0 && step < p_julia->iterations)
			{
				complex z_aux = {.a = z.a, .b = z.b};

				z.a = pow(z_aux.a, 2) - pow(z_aux.b, 2) + p_julia->c_julia.a;
				z.b = 2 * z_aux.a * z_aux.b + p_julia->c_julia.b;

				step++;
			}

			result_julia[height_julia - h - 1][w] = step % 256;
		}
	}
}

void run_mandelbrot(int start, int end) {
	int h, w;
	for (w = 0; w < width_mandel; w++)
	{
		for (h = start; h < end; h++)
		{
			complex c = {.a = w * p_mandel->resolution + p_mandel->x_min,
						 .b = h * p_mandel->resolution + p_mandel->y_min};
			complex z = {.a = 0, .b = 0};
			int step = 0;

			while (sqrt(pow(z.a, 2.0) + pow(z.b, 2.0)) < 2.0 && step < p_mandel->iterations)
			{
				complex z_aux = {.a = z.a, .b = z.b};

				z.a = pow(z_aux.a, 2.0) - pow(z_aux.b, 2.0) + c.a;
				z.b = 2.0 * z_aux.a * z_aux.b + c.b;

				step++;
			}

			result_mandel[height_mandel - h - 1][w] = step % 256;
		}
	}
}

void *thread_function(void *arg)
{
	int tid = *(int *)arg;

	int start_jul_h = tid * (double)height_julia / P;
	int end_jul_h = MIN((tid + 1) * (double)height_julia / P, height_julia);
	run_julia(start_jul_h, end_jul_h);
	
	pthread_barrier_wait(&barrier);

	int start_man_h = tid * (double)height_mandel / P;
	int end_man_h = MIN((tid + 1) * (double)height_mandel / P, height_mandel);
	run_mandelbrot(start_man_h, end_man_h);

	pthread_exit(NULL);
}

int main(int argc, char *argv[])
{
	// se citesc argumentele programului
	get_args(argc, argv);

	int tid;
	int thread_args[P];
	pthread_t threads[P];

	read_and_init_julia();
	read_and_init_mandel();    
	pthread_barrier_init(&barrier, NULL, P);

	for (tid = 0; tid < P; tid++)
	{
		thread_args[tid] = tid;
		pthread_create(&threads[tid], NULL, thread_function, &thread_args[tid]);
	}

	// se asteapta thread-urile
	for (tid = 0; tid < P; tid++)
		pthread_join(threads[tid], NULL);

	write_output_file(out_filename_julia, result_julia, width_julia, height_julia);
	write_output_file(out_filename_mandelbrot, result_mandel, width_mandel, height_mandel);
	free_resources();

	return 0;
}
