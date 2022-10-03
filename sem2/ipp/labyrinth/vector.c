#include <stdlib.h>

#include "vector.h"
#include "errors.h"

void push_back(VPtr vector, ul element) {
    if (vector->actualLength + 1 > vector->allocatedLength) {
        vector->array = realloc((vector->array), (ul) 2 * vector->allocatedLength * sizeof(ul));
        vector->allocatedLength *= (ul)2;

        if (vector->array == NULL)
            memoryError(0);
    }

    vector->array[vector->actualLength] = element;
    (vector->actualLength)++;
}

void initializeVector(VPtr *vector) {
    *vector = malloc(sizeof(struct Vector));

    (*vector)->array = malloc(sizeof(ul));

    (*vector)->allocatedLength = 1;
    (*vector)->actualLength = 0;
}

void freeVector(VPtr vector) {
    free(vector->array);
    free(vector);
}
