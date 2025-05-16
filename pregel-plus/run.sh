TMP_FOLDER=/tmp
INPUT_FOLDER=$TMP_FOLDER/input
GOOD_GRAPH=$INPUT_FOLDER/graph.pp

ALGO=$1
RAW_GRAPH=$2


TOTAL_ARGS="$#"
ARGS_NUM_AFTER_3=$(( TOTAL_ARGS - 2 ))
ARGS_AFTER_3="${@:3}"

mkdir -p $INPUT_FOLDER

case $ALGO in
  "boruvka")
    python ../helpers/dimacs2pp_boruvka.py $RAW_GRAPH $GOOD_GRAPH
    ;;
  
  "msbfs")
    EXT="${RAW_GRAPH##*.}"
    case "$EXT" in
      "txt")
        python ../helpers/snaptxt2pp_pbfs.py $RAW_GRAPH > $GOOD_GRAPH
        ;;
      "json")
        python ../helpers/snapjson2pp_pbfs.py $RAW_GRAPH $GOOD_GRAPH
        ;;
      *)
        echo "--> Detected: Unknown or unhandled EXT value: '$EXT'" 
        return -1
        ;;
    esac
    

    NEW_LINE="p\t$ARGS_NUM_AFTER_3 $ARGS_AFTER_3\n"
    sed -i "1s/^/$NEW_LINE/" $GOOD_GRAPH
    ;;

  *)
    echo "--> Detected: Unknown or unhandled ALGO value: '$ALGO'" 
    return -1
    ;;
esac

$HADOOP_HOME/bin/hadoop fs -put -f $INPUT_FOLDER /

make run_$ALGO

rm -r ./output
$HADOOP_HOME/bin/hadoop fs -get -f /output