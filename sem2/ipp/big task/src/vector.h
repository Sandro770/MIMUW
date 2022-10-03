#ifndef PHONE_NUMBERS_VECTOR_H
#define PHONE_NUMBERS_VECTOR_H

#include "stddef.h"
#include "trie.h"
#include "stdbool.h"

typedef struct Node Node;

/** @brief Struktura reprezentująca dynamiczną tablicę.
 *  Struktura reprezentująca dynamicznie rozszerzającą się tablicę.
 */
struct Vector{
    size_t length; /**< obecna długość tablicy */
    size_t reserved; /**< zaalkowana długość tablicy */
    Node **elements; /**< wskaźnik na tablicę elementów */
};

typedef struct Vector Vector;/**< lukier syntaktyczny*/

Vector *getEmptyVector();

bool insert(Vector *pVector, Node *elem);

void setFirstPos(Vector *pVector, Node *elem);

void erase(Vector *pVector, size_t pos);

void freeVector(Vector *pVector);

Node *getById(Vector *pVector, size_t id);

size_t sizeOfAVector(Vector *pVector);

void eraseByValue(Vector *pVector, Node *val);

#endif //PHONE_NUMBERS_VECTOR_H
