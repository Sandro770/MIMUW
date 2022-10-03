# Labyrinth - small task

This is smaller project from Individual Programming Project course on University of Warsaw.

This program is finding the shortest path between 2 points in multidimensional graph.

## Main file
labyrinth.c

## Testing script
``` 
./test.sh prog dir 
```
**prog** - path to labyrinth.c file  <br />
**dir** - path to directory with tests  <br />

Tests in **dir** should be in format *.in, *.out, *.err 

## Other important informations

### Challenging part
The hardest part was the input format - the graph could be described explicitly or by a formula.
There were also different encodings of the input.
The program is also handling the incorrect input, and giving adequate errors.

### Error handling
The program is handling input errors as well as not enough memory errors.