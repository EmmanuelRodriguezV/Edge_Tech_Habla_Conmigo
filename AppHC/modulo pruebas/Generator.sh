
for entry in `ls SENAS`/*; do
    
    nombre="$entry _Nueva";
    echo $nombre;
    python Ajustador_Datos.py SENAS/$entry  $nombre  SENAS_NUEVAS/$entry
    echo "se termino de processar la letra $entry";
done