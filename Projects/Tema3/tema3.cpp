#include <cstdio>
#include <cstdlib>
#include <iostream>
#include <fstream>
#include <pthread.h>
#include "mpi.h"

#include <unordered_map>
#include <functional>

#define P 8
#define MASTER_THREADS 4

#define THREAD_HORROR 0
#define THREAD_COMEDY 1
#define THREAD_FANTASY 2
#define THREAD_SCIFI 3

#define MASTER 0
#define WORKER_HORROR 1
#define WORKER_COMEDY 2
#define WORKER_FANTASY 3
#define WORKER_SCIFI 4

#define GENRE_HORROR "horror"
#define GENRE_COMEDY "comedy"
#define GENRE_FANTASY "fantasy"
#define GENRE_SCIFI "science-fiction"

#define END_COMM "END!"

typedef struct {
    int thread_id;
    char* file_name;
    std::unordered_map<int, std::string>* order_map;
    int* paragraph_idx;
} Args; 

typedef struct {
    int thread_id;
    int worker_rank;
} WorkerArgs; 

bool isConsonant(char ch) 
{ 
    ch = toupper(ch); 
    return !(ch == 'A' 
            || ch == 'E'
            || ch == 'I' 
            || ch == 'O'
            || ch == 'U'); 
} 
 
bool isSpecialChar(char c) {
    return c == '.' || c== ',' || c == '?' || c == '!';
}

std::string format_horror_str(char* str) {
    std::string res = "";
    
    int i = 0;
    while (i < strlen(str) && str[i] != '\n') {
        res += str[i];
        i++;
    }
    res += '\n';
    i++;
    for (; i < strlen(str); i++) {
        if (isConsonant(str[i]) && str[i] != '\n' && !isSpecialChar(str[i]) && str[i] != ' ') {
            res += str[i]; 
            res += tolower(str[i]); 
        }  else {
            res += str[i]; 
        }
    } 
  
    return res;
}

std::string format_comedy_str(char* str) {
    std::string res = "";

    int i = 0;
    while (i < strlen(str) && str[i] != '\n') {
        res += str[i]; 
        i++;
    }

    int word_st = 0;
    for (; i < strlen(str); i++) {
        if (str[i] == ' ' || str[i] == '\n')
            word_st = 0;

        if (word_st % 2 == 0) {
            res += toupper(str[i]);
        } else {
            res += str[i];
        }
        word_st++;
    }

    return res;
}

std::string format_fantasy_str(char* str) {
    std::string res = "";

    int i = 0;
    while (i < strlen(str) && str[i] != '\n') {
        res += str[i]; 
        i++;
    }

    bool start_line = true;
    for (; i < strlen(str); i++) {
        if (str[i - 1] == ' ' || str[i - 1] == '\n' || start_line) {
            start_line = false;
            res += toupper(str[i]);
        } else {
            res += str[i];
        }

        if (str[i] == '\n') {
            start_line = true;
        }
    }

    return res;
}

std::string format_scifi_str(char* str) {
    std::string res = "";

    int i = 0, j, k;
    int wordCounter = 1;

    while (str[i] != '\0') {
        if (str[i] == '\n') {
            wordCounter = 1;
        }

        if (str[i] == ' ') {
            wordCounter++;
        }

        res += str[i];
        i++;

        if (wordCounter == 7) {
            j = i;
            while (str[j] != ' ' 
                && str[j] != '\n' 
                && str[j] != '\0'
                && str[i] != '.' 
                && str[i] != ','
                && str[i] != '!'
                && str[i] != '?') {
                j++;
            }

            k = j;
            for (; j > i;) {
                res += str[--j];
            }
            i = k;
            wordCounter = 0;
        }
    }

    return res;
}

void *thread_function_worker(void *arg) {
    WorkerArgs args = *(WorkerArgs*) arg;
    MPI_Status status;

    int buffer_size = 0;
    char* recv_buffer;
    bool can_recv = true;

    while (can_recv) {
        MPI_Probe(MASTER, 0, MPI_COMM_WORLD, &status);
        MPI_Get_count(&status, MPI_CHAR, &buffer_size);

        recv_buffer = (char* )calloc(buffer_size, sizeof(char));
        MPI_Recv(recv_buffer, buffer_size, MPI_CHAR, MASTER, 0, MPI_COMM_WORLD, &status);

        if (strcmp(recv_buffer, END_COMM) == 0) {
            can_recv = false;
        } else {
            // TODO:
            // count lines -> split work in max P-1 threads
            // P - 1 threads -> processing data
            // 20 lines -> 1 thread processing them (max P-1)

            std::string formatted_genre_str;

            if (args.worker_rank == WORKER_HORROR) {
                formatted_genre_str.append(format_horror_str(recv_buffer));
            } else if (args.worker_rank == WORKER_FANTASY) {
                formatted_genre_str.append(format_fantasy_str(recv_buffer));
            } else if (args.worker_rank == WORKER_COMEDY) {
                formatted_genre_str.append(format_comedy_str(recv_buffer));
            } else {
                formatted_genre_str.append(format_scifi_str(recv_buffer));
            }

            // TODO: Merge their results

            // Send formated paragraph merged to Master Node
            MPI_Send(formatted_genre_str.c_str(), formatted_genre_str.size(), MPI_CHAR, MASTER, 0, MPI_COMM_WORLD);
        }
    }

    pthread_exit(NULL);
}

void read_paragraphs_for_genre(Args& args, std::string genre, int worker_rank) {
    std::ifstream in_file;
    in_file.open(args.file_name);

    std::string buffer;
    std::string line;

    MPI_Status status;
    int buffer_size = 0;
    char* recv_buffer;

    int paragraphs = 0;

    if (in_file.is_open()) {
        while (std::getline(in_file, line)) {
            if (line.compare(genre) == 0) {
                // Push genre to a global queue in order to keep order of paraghps' genres
                buffer.append(genre).append("\n");

                while (std::getline(in_file, line) && line.compare("") != 0) {
                    buffer.append(line).append("\n");
                }
                // Send paragraph to be formatted
                MPI_Send(buffer.c_str(), buffer.size(), MPI_CHAR, worker_rank, 0, MPI_COMM_WORLD);
                buffer.clear();

                // Recv paragraph after formatting
                MPI_Probe(worker_rank, 0, MPI_COMM_WORLD, &status);
                MPI_Get_count(&status, MPI_CHAR, &buffer_size);
                recv_buffer = (char* )calloc(buffer_size, sizeof(char));
                MPI_Recv(recv_buffer, buffer_size, MPI_CHAR, worker_rank, 0, MPI_COMM_WORLD, &status);
                args.order_map->insert({paragraphs, recv_buffer});
            }
            if (line.compare("") == 0) {
                paragraphs++;
            }
        }
    }

    in_file.close();

    MPI_Send(END_COMM, strlen(END_COMM), MPI_CHAR, worker_rank, 0, MPI_COMM_WORLD);
}

void *thread_function_master(void *arg) {
    Args args = *(Args*) arg;

    if (args.thread_id == THREAD_HORROR) {
        read_paragraphs_for_genre(std::ref(args), GENRE_HORROR, WORKER_HORROR);
    } else if (args.thread_id == THREAD_COMEDY) {
        read_paragraphs_for_genre(std::ref(args), GENRE_COMEDY, WORKER_COMEDY);
    } else if (args.thread_id == THREAD_FANTASY) {
        read_paragraphs_for_genre(std::ref(args), GENRE_FANTASY, WORKER_FANTASY);
    } else {
        read_paragraphs_for_genre(std::ref(args), GENRE_SCIFI, WORKER_SCIFI);
    }

	pthread_exit(NULL);
}

void start_reading_thread_from_worker(int worker_rank) {
    pthread_t reader_thread;
    WorkerArgs args;
    args.thread_id = 0;
    args.worker_rank = worker_rank;

    pthread_create(&reader_thread, NULL, thread_function_worker, &args);

    pthread_join(reader_thread, NULL);
}

void check_input_args(int argc) {
    if (argc < 2) {
        std::cout << "Wrong number of args." << std::endl << "e.g: ./main <file_name>" << std::endl;
        exit(1);
    }
}

int main (int argc, char *argv[])
{
    check_input_args(argc);
    int num_tasks, rank;
    int provided;
    MPI_Init_thread(&argc, &argv, MPI_THREAD_MULTIPLE, &provided);

    MPI_Comm_size(MPI_COMM_WORLD, &num_tasks);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Status status;

    if (rank == MASTER) {
        pthread_t threads[MASTER_THREADS];
	    Args thread_args[MASTER_THREADS];
	
        std::unordered_map<int, std::string> order_map;
        int p_idx = 0;

        for (int i = 0; i < MASTER_THREADS; i++) {
            thread_args[i].thread_id = i;
            thread_args[i].file_name = argv[1];
            thread_args[i].order_map = &order_map;
            thread_args[i].paragraph_idx = &p_idx;
            pthread_create(&threads[i], NULL, thread_function_master, &thread_args[i]);
        }

        for (int i = 0; i < MASTER_THREADS; i++) {
            pthread_join(threads[i], NULL);
        }

        // Write output to file
        std::ofstream out_file;
        std::string out_file_name(argv[1]);
        out_file_name[18] = 'o';
        out_file_name[19] = 'u';
        out_file_name[20] = 't';

        out_file.open(out_file_name, std::fstream::out);
        for (int i = 0; i < order_map.size(); i++)
            out_file << order_map[i] << std::endl;
        out_file.close();
    } else if (rank == WORKER_HORROR) {
        start_reading_thread_from_worker(WORKER_HORROR);
    } else if (rank == WORKER_COMEDY) {
        start_reading_thread_from_worker(WORKER_COMEDY);
    } else if (rank == WORKER_FANTASY) {
        start_reading_thread_from_worker(WORKER_FANTASY);
    } else if (rank == WORKER_SCIFI) {
        start_reading_thread_from_worker(WORKER_SCIFI);
    }

    MPI_Finalize();
    return 0;
}
