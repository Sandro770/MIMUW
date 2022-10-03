#include <malloc.h>
#include <stdbool.h>
#include "vector.h"

bool resize(Vector *pVector) {
    if (pVector->length > pVector->reserved) {
        pVector->reserved *= (size_t) 2;

        Node **tmp = realloc(pVector->elements, pVector->reserved * sizeof
        (Node *));

        if (tmp == NULL)
            return false;

        pVector->elements = tmp;
    }

    return true;
}

bool insert(Vector *pVector, Node *elem) {
    pVector->length++;

    if (!resize(pVector)) {
        pVector->length--;
        return false;
    }

    pVector->elements[pVector->length - 1] = elem;

    return true;
}

void erase(Vector *pVector, size_t pos) {
    if (pos < pVector->length) {
        pVector->elements[pos] = pVector->elements[pVector->length - 1];
        pVector->length--;
    }
}

Vector *getEmptyVector() {
    Vector *tmp = malloc(sizeof(Vector));

    if (tmp == NULL)
        return NULL;

    tmp->length = 0;
    tmp->reserved= 1;
    tmp->elements = malloc(sizeof(Node *));

    if (tmp->elements == NULL) {
        free(tmp);
        return NULL;
    }

    return tmp;
}

void freeVector(Vector *pVector) {
    free(pVector->elements);
    free(pVector);
}

Node * getById(Vector *pVector, size_t id) {
    if (id >= pVector->length)
        return NULL;

    return pVector->elements[id];
}

size_t sizeOfAVector(Vector *pVector) {
    return pVector->length;
}

void setFirstPos(Vector *pVector, Node *elem) {
    pVector->elements[0] = elem;

    if (pVector->length == 0)
        pVector->length++;
}

void eraseByValue(Vector *pVector, Node *val) {
    for (size_t i = 0; i < pVector->length; i++)
        if (pVector->elements[i] == val) {
            erase(pVector, i);
            return;
        }
}