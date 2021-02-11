Cotovanu Cristian 336CA

Implementare:
Am pornit din main, 
am facut citirea in paralel -> deschid cele 4 threaduri pentru fiecare gen
si fisierul de input din fiecare dintre ele, urmand sa fac citirea.
Citesc paragraf cu paragraf, trimit paragraful catre workerul specific, apoi
astept raspunsul printr-un MPIRecv, dimensiunile bufferului de recv le obtin
folosind functiile Mpi_probe si Mpi_get_count

In workeri am pornit un singur thread initial (urmand din el sa pornesc threadurile pentru paralelizarea
formatarii textului). 
Sincronizarea este facut in main, am un map in care Master-ul cand 
primeste un paragraf formatat de workeri il pune in acest map conform index-ului