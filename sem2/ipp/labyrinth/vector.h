#ifndef LABYRINTH_VECTOR_H
#define LABYRINTH_VECTOR_H

#include "definitions.h"

struct Vector{
    ul * array;
    ul allocatedLength;
    ul actualLength;
};

typedef struct Vector *VPtr;

void push_back(VPtr vector, ul element);

void initializeVector(VPtr *vector);

void freeVector(VPtr vector);

#endif //LABYRINTH_VECTOR_H
