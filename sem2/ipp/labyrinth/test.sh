#!/bin/bash

program=$1;
dir=$2;

dir="${dir%/}";

for f in "$dir"/*.in; do
  err=$(mktemp);
  out=$(mktemp);
  valgrindOutput=$(mktemp);

  fileName="${f#$dir/}";
  fileName="${fileName%.in}";

  valgrind --error-exitcode=123 \
           --leak-check=full \
           --show-leak-kinds=all \
           --errors-for-leak-kinds=all \
           --log-file="$valgrindOutput" \
           ./"$program" < "$f" 1> "$out" 2> "$err";

  if [ $? == 123 ]; then
    echo "There was an memory error in $fileName";
#    cat "$valgrindOutput"; #uncomment this line to see the error
  fi;

  if (diff "$out" "${f%in}out" >/dev/null) && \
     (diff "$err" "${f%in}err" >/dev/null); \
    then
      echo "Test $fileName passed";
  else
    echo "Test $fileName didn't pass";
  fi

  rm "$err";
  rm "$out";
  rm "$valgrindOutput";
done;
