Cristian Cotovanu 336CA 

IDEE:
Am numarat caracterele din fisier
Apoi am impartit fiecarui thread un numar de iteratii de citire pentru un chunk de L litere.
Numarul de iteratii pentru fiecare thread vine sub formula chars_in_file / P / L + 1.

 Am utilizat un mutex cand se citeste o litera din fisier deoarece se pot intersecta 2 threads si se pierede un caracter