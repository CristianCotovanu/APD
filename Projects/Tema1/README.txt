Cristian Cotovanu 336CAa

APD - Tema1

In functia main se citesc argumentele, se initializeaza array-ul de thread-uri cat
si cel de argumente pentru thread-uri.
Am declarat global variabilele necesare in thread_function pentru cei 2 algoritmi
(height, width, result, params) cat si bariera.

Citesc datele necesare din cele doua fisiere in variabilele globale asociate
celor doua.
Initializez bariera.

Creez thread-urile
Paralelizarea am facut-o pe inaltime/linii (height) si anume in functie de start si
end index calculati in functie de numarul de threads (citite din input). 
Intre cei doi algoritmi am aplicat o bariera in thread_function.
Transformarea rezultatului din coordonate matematice in coordonate ecran nu o voi 
mai face prin mai multe swap-uri ci voi scrie in result direct in coordonate ecran.

Unesc thread-urile create anterior cu thread-ul main.

Scriu rezultatul pentru cele 2 fisiere de out.
Eliberez resursele alocate pentru matricile result cat si bariera.

Am incercat sa modularizez codul cat de mult posibil.

O alta abordare posibila ar fi fost paralelizarea alocarii matricilor result
apoi aplicarea unei bariere si fiecare thread lucra pe spatiul alocat anterior
Paralelizarea algoritmilor apoi identica cu cea de mai sus.
