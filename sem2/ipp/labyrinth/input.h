#ifndef LABYRINTH_INPUT_H
#define LABYRINTH_INPUT_H

#include "definitions.h"

struct Visited{
    uint32_t *array;
    uint32_t length;
};

typedef struct Visited* PVisited;

struct Input{
    ul *n;
    ul *x;
    ul *y;
    ul k;
    PVisited pVisited;
};

typedef struct Input* PInput;


void initializeInput(PInput *pInput);

bool readInput(PInput pInput);

void freeMemory(PInput pInput);

ul getIndexInVisited(ul j);

uint32_t getBitInVisited(ul j);

ul getNProduct(PInput pInput);

bool wasVisited(PInput pInput, ul vertex);

ul cordsToNumber(PInput pInput, ul *cords);

#endif //LABYRINTH_INPUT_H
