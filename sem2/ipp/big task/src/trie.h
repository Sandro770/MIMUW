#ifndef PHONE_NUMBERS_TRIE_H
#define PHONE_NUMBERS_TRIE_H

#include "vector.h"

/**
 * Maksymalna ilość synów jaką może mieć wierzchołek w drzewie trie.
 */
#define NB_OF_SONS 12

struct Node;
typedef struct Node Node; /**< lukier syntaktyczny */
typedef struct Vector Vector;

/** @brief To jest struktura reprezentująca wierzchołek drzewa trie.
 * To jest struktura reprezentująca wierzchołek drzewa trie, które
 * przechowuje numery telefonów składających się z cyfr 0..9, wraz z
 * przekierowaniami. Jeśli forward ma wartość NULL, to nie ma
 * przekierowania.
 */
struct Node{
    Node *parent; /**< wskaźnik na ojca w drzewie */
    Node *sons[NB_OF_SONS]; /**< tablica z wskaźnikami na synów */
    Vector *forward; /**< wskaźnik na vector przekierowań */

    int depth; /**< głębokość drzewa lioczna od korzenia */
    char lastC; /**< ostatnia cyfra napisu reprezentowanego przez wierzchołek */

    int nxtInTreeTraversal; /**< numer wierzchołka, do którego mamy obecnie
 * przejść */
};


#endif //PHONE_NUMBERS_TRIE_H
