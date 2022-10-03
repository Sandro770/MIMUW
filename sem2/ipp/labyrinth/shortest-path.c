#include "shortest-path.h"
#include "vector.h"
#include "errors.h"

#define INFINITY -1

void tryToAdd(VPtr vector, ul vertex, PVisited visited);

VPtr getSuccessors(VPtr pVector, PInput pInput);

ul * getArrayRepresentation(ul vertex, PInput pInput);

void printShortestPath(long shortestPath) {
    if (shortestPath == INFINITY)
        printf("NO WAY\n");
    else
        printf("%ld\n", shortestPath);
}

long findShortestPath(PInput inputPtr) {
    ul start = cordsToNumber(inputPtr, inputPtr->x);
    ul destination = cordsToNumber(inputPtr, inputPtr->y);

    VPtr actualWave, successors;

    initializeVector(&actualWave);

    tryToAdd(actualWave, start, inputPtr->pVisited);

    long currentDist = 0;

    while (actualWave->actualLength > 0) {
        if (wasVisited(inputPtr, destination))
            break;

        currentDist++;

        successors = getSuccessors(actualWave, inputPtr);

        freeVector(actualWave);
        actualWave = successors;
    }

    freeVector(actualWave);

    if (wasVisited(inputPtr, destination))
        return currentDist;
    else
        return INFINITY;
}

VPtr getSuccessors(VPtr pVector, PInput pInput) {
    VPtr successors;
    initializeVector(&successors);

    for (ul i = 0; i < pVector->actualLength; i++) {
        ul actualVertex = pVector->array[i];

        ul * cords = getArrayRepresentation(actualVertex, pInput);

        ul product = 1;

        for (ul j = 0; j < pInput->k; j++) {
            ul neighbour;
            if (cords[j] > 1) {
                neighbour = actualVertex - product;
                tryToAdd(successors, neighbour, pInput->pVisited);
            }
            if (cords[j] < pInput->n[j]) {
                neighbour = actualVertex + product;
                tryToAdd(successors, neighbour, pInput->pVisited);
            }

            product *= pInput->n[j];
        }

        free(cords);
    }

    return successors;
}

ul* getArrayRepresentation(ul vertex, PInput pInput) {
    ul product = getNProduct(pInput);

    ul * cords = malloc((pInput->k) * sizeof(ul));
    if (cords == NULL)
        memoryError();

    for (long i = pInput->k - 1; i >= 0; i--) {
        product /= pInput->n[i];

        cords[i] = 1 + vertex / product;

        vertex -= (cords[i] - 1LU) * product;
    }

    return cords;
}

void tryToAdd(VPtr vector, ul vertex, PVisited visited) {
    ul id = getIndexInVisited(vertex);
    uint32_t bit = getBitInVisited(vertex);

    if ((visited->array[id] & bit) == 0) {
        push_back(vector, vertex);
        visited->array[id] |= bit;
    }
}