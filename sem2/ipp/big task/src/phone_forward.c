/** @file
 * Implementacja klasy przechowującej przekierowania numerów telefonicznych
 *
 * @author Alessandro Peria <alessandroperia@icloud.com>
 * @copyright Uniwersytet Warszawski
 * @date 2022
 */

#include <string.h>
#include <stdlib.h>
#include "phone_forward.h"

#include "trie.h"


/**
 * Funkcje, które mają być wywołane na wierzchołkach w poddrzewie drzewa trie.
 */
enum FunctionType{RemoveForwarding /**< funkcja ustawiająca forward na NULL*/,
        DeleteNode /**
 * @ref deleteNode */};
typedef enum FunctionType FunctionType; /**< lukier syntaktyczny */

/**
 * To jest struktura przechowująca przekerowania numerów telefonów w drzewie
 * trie.
 */
struct PhoneForward {
    Node *trie; /**< drzewo przekierowań */
    Node *revTrie; /**< drzewo przekierowań z zamienionymi kolejnością
 * przekierowaniami*/
};

/**
 * To jest struktura przechowująca numery telefonów.
 */
struct PhoneNumbers {
    const char **numbers; /**< wskaźnik na tablicę numerów telefonów */
    size_t total; /**< aktualna liczba numerów telefonów w strukturze */
    size_t capacity; /**< aktualny rozmiar talicy @ref numbers */
};

/** @brief Sprawdza, czy numer jest prawidłowy.
 * Sprawdza czy @p num1 jest niepustym ciągiem składającym się tylko z cyfr '0'
 * ..'9', za którym jest znak '\0'.
 * @param num1
 * @return Wartość @p true jeśli @p num1 spełnia definicję wyżej.
 *         Wartość @p false jesli @p num1 nie spełnia definicji wyżej.
 */
bool isCorrectNumber(const char *num1);

/** @brief Przechodzi przez poddrzewo i wywołuje na wierzchołkach funkcję
 * @p type.
 * Przechodzi DFS'em przez poddrzewo @p tree i wywołuje na wierzchołkach
 * różnych od
 * NULL funkcję @p type.
 * @param tree - wskaźnik na wierzchołek w drzewie trie
 * @param type - typ operacji
 * @param revTrie - wskaźnik na korzeń revTrie
 */
void treeTraversal(Node *tree, FunctionType type, Node *revTrie);

/** @brief Zwraca wskaźnik na wierzchołek reprezentujący @p num w drzewie trie
 * @p actNode.
 * Zwraca wskaźnik do wierzchołka reprezentującego @p num w drzewie trie
 * @p actNode, przy okazji w razie potrzeby tworząc nowe wierczchołki.
 * @param actNode
 * @param num
 * @return Wartość @p NULL jeśli @p num nie jest numerem lub doszło do
 * problemów z alokacją pamięci w trakcie.
 *         Wskaźnik na wierzchołek, który reprezentuje ostatnią cyfrę napisu
 *         @p num w drzewie @p actNode.
 */
Node *getNode(Node *actNode, const char *num);

/** @brief Tworzy numer telefonu odpowiadający wierzchołkowi @p pNode w
 * drzewie trie.
 * Tworzy numer telefonu odpowiadający wierzchołki @p pNode w drzewie trie,
 * który trzeba później zwolnić funkcją free.
 * @param pNode
 * @return Wartość NULL jeśli nie udało się zaalokować pamięci.
 *         Wskaźnik na numer odpowiadający wierzchołkowi @p pNode.
 */
const char *getNum(Node *pNode);

/** @brief Znajduje najdłuższy prefiks numer @p num, który ma przekierowanie.
 * @param pNode - wskaźnik na korzeń drzewa trie;
 * @param num - wskaźnik na numer, którego szukamy przekierowania;
 * @return Wartość NULL jeśli nie ma takiego prefiksu.
 *         Wskaźnik na szukany wierzchołek, jeśli taki istnieje.
 */
Node *getMaxPrefixWithForwarding(Node *pNode, const char *num);

/**
 * @param pNode - wskaźnik na badany wierzchołek.
 * @return Wartość true gdy pNode jest równy NULL.
 *         Wartość false w przeciwnym przypadku.
 */
bool isRoot(Node *pNode);

/** Sprawdza czy c to znak '\0'.
 * @param c
 * @return Wartośc true jeśli c ma wartość '\0'.
 *         Wartość false w przeciwnym przypadku.
*/
bool isEmptyNum(char c);

/** @brief Przepisuje napis odpowiadający wierzchołkowi @p pNode do @p num.
 * Przepisuje napis odpowiadający wierzchołkowi @p pNode do @p num, dodając
 * na samym końcu znak '\0'.
 * @param pNode - wskaźnik na wierzchołek odpowiadający numerowi;
 * @param num - wskaźnik na zaalokowaną tablicę o rozmiarze @p length, do
 * której mamy wpisać numer.
 * @param length - długość numeru, który reprezentuje @p pNode powiększona o
 * 1, bo znak '\0'.
 */
void getNumIntoArray(Node *pNode, char *num, size_t length);

/** @brief Zwraca numer na jaki został przekierowany @p num.
 * @param maxPrefix - wskaźnik na wierzchołek w drzewie, z którego jest
 * przekierowanie na zmianę prefiksu. Musi istnieć z niego przekierowanie.
 * @param num - prawdiłowy numer telefonu.
 * @return Wskaźnik na numer powstały zgodnie z opisem @ref phfwdAdd lub NULL,
 * jeśli doszło do problemu z alokacją pamięci.
 */
const char *getForwardedNumWithMaxPrefix(Node *maxPrefix, const char *num);

/** @brief Zwraca długość numeru jaki powstał po przekierowaniu.
 * Zwraca długość numeru jaki powstał po przekierowaniu zgodnie z @ref
 * phfwdAdd.
 * @param pastPrefixLen - długość jaką daje funkcja strlen();
 * @param newPrefixLen - długość jaką daje funkcja strlen();
 * @param pastWordLen - długość jaką daje funkcja strlen().
 * @return zwraca długość nowego prefiksu nie licząc znaku '\0'.
 */
size_t
getNewNumLength(size_t pastPrefixLen, size_t newPrefixLen, size_t pastWordLen);

/** @brief Zamienia prefiks numeru telefonu.
 * Tworzy nowy numer telefonu, który jest konkatenacją @p newPrefix oraz
 * sufiksu @p string, gdzie sufiks @p string to @p string bez @p pastPrefix
 * na początku.
 * @param pastPrefix - numer telefonu, który jest prefiksem modyfikowanego
 * numeru telefonu;
 * @param newPrefix - numer telefonu, który jest prefiksem przekierowanego
 * numeru telefonu;
 * @param string - numer telefonu, który ma być przekierowany.
 * @return Wskaźnik na numer telefonu ze zmienionym prefiksem lub NULL, gdy
 * doszło do problemu z alokacją pamięci.
 */
char *
changePrefix(const char *pastPrefix, const char *newPrefix, const char *string);

/** @brief Usuwa przekierowania z poddrzewa @p tree.
 *  Usuwa przekierowania z poddrzewa @p tree, poprzez ustawienie ich na NULL.
 * @param nodeFromTrie - wskaźnik na poddrzewo, z którego usuwamy przekierowania.
 * @param revTrie - wskaźnik na korzeń revTrie
 */
void
eraseForwardingsInTree(Node *nodeFromTrie, Node *revTrie);

/** @brief Tworzy pustą strukturę @ref PhoneNumbers.
 * Tworzy zainicjalizowaną pustą strukturę @ref PhoneNumbers, która musi być
 * zwolniona za
 * pomocą funkcji @ref phnumDelete.
 * @return Wskaźnik na pustą strukturę lub NULL, gdy doszło do problemu z
 * alokacją pamięci.
 */
PhoneNumbers *getEmptyPnum();

/** Podwaja ilość możliwych numerów do przechowania w @p pnum.
 * @param pnum - wskaźnik na @ref PhoneNumbers, który nie jest NULL'em.
 * @return Wartość true, jeśli udała się operacja, false w przeciwnym przypadku.
 */
bool resizePnum(PhoneNumbers *pnum);

/** @brief Dodaje nowy numer telefonu.
 * Dodaje nowy numer telefonu do @p pnums.
 * @param pnums - wskaźnik na strukturę przechowującą numery telefonów;
 * @param num - wskaźnik na numer telefonu, który chcemy dodać. Musi on być
 * prawidłowym numerem telefonu.
 * @return Wartość true jeśli operacja udała się, false w przeciwnym przypadku.
 */
bool pushBackPnum(PhoneNumbers *pnums, const char * num);

/** Sprawdza czy @c jest cyfrą.
 * @param c
 * @return Wartość true, jeśli @c jest cyfrą, false w przeciwnym przypadku.
 */
bool isDigit(char c);

/** Zwraca kopię numeru telefonu @p num.
 * Kopiuje numer telefonu @p num w nowe miejsce w pamięci.
 * @param num - poprawny numer telefonu.
 * @return Wskaźnik do kopii numeru @p num lub NULL, gdy doszło do problemu z
 * alokacją pamięci.
 */
const char *strDuplicate(const char *num);

/** Sprawdza, czy @p pNode ma przekierowanie do innego numeru telefonu.
 * @param pNode - wskaźnik na wierzchołek w drzewie, różny od NULL.
 * @return Wartość true, jeśli ma przekierowanie, false w przeciwnym przypadku.
 */
bool hasForwarding(Node *pNode);

/** @brief Tworzy strukturę z numerem telefonu @p num.
 * Tworzy strukturę z numerem telefonu @p num, jeśli udało się utworzyć
 * strukture, to musi ona być później usunięta przez @ref phnumDelete.
 * @param num - prawidłowy numer telefonu.
 * @return Wskaźnik na strukturę z numerem telefonu @p num lub NULL, gdy
 * nie udało się zaalokować pamięci.
 */
PhoneNumbers *getSinglePhnum(const char *num);

/** Wykonuje funkcję @p type na wierzchołku @p node.
 * Wykonuje funkcję @p type na wierzchołku @p node, w przypadku gdy node ma
 * wartość NULL nic nie robi.
 * @param node - wskaźnik na wierzchołek;
 * @param type - typ funkcji, która ma byc wykonana;
 * @param revTrie - wskaźnik na korzeń drzewa revTrie.
 */
void functionOnNode(Node *node, FunctionType type, Node *revTrie);

/** Sprawdza czy wierzchołek jest liściem.
 * @param pNode - wskaźnik na wierzchołek, który sprawdzamy. Musi byc różny
 * od NULL.
 * @return Wartość true, jeśli jest liściem, false w przeciwnym przypadku.
 */
bool noSons(Node *pNode);

/** @brief Usuwa wierzchołek.
 *  Usuwa wierzchołek wskazywany przez @p pNode.
 * @param pNode - wskaźnik na wierzchołek.
 */
void deleteNode(Node *pNode);

/** @brief Tworzy nowy wierzchołek.
 * Tworzy nowy wierzchołek, który musi być później usunięty za pomocą @ref
 * deleteNode.
 * @return Wskaźnik do nowego wierzchołka lub NULL, gdy nie udało się
 * zaalokować pamięci.
 */
Node *getPtrToNewNode();

/** @brief Dodaje syna do wierzchołka.
 * Dodaje syna do wierzchołka.
 * @param node - wskaźnik do wierzchołka, któremu mamy dodać syna.
 * Wskaźnik musi być różny od NULL oraz nie może mieć już syna o zadanym @p
 * sonId.
 * @param sonId - numer syna od 0 do 9.
 * @return Wartość true, jeśli udała się operacja, false w przeciwnym przypadku.
 */
bool addSonToNode(Node *node, int sonId);

/** Wyznacza numer następnego wierzchołka.
 * Wyznacza numer następnego wierzchołka, który powinniśmy odwiedzić.
 * Gdzie numer -1 oznacza, ojca, a pozostałe: 0..9 oznaczają numery synów.
 * @param pastId - numer aktualnego wierzchołka.
 * @return
 */
int getNextNodeDFSId(int pastId);

/** @brief Udostępnia wskaźnik na następnym wierzchołek do odwiedzenia.
 * Udostępnia wskaźnik na następnym wierzchołek do odwiedzenia w kolejności DFS.
 * @param node - wskaźnik na aktualny wierzchołek, różny od NULL.
 * @return wskaźnik na następny wierzchołek do odwiedzenia, być może NULL.
 */
Node * nextNodeDFS(Node * node);

const char *getForwardedNumByForwardingId(Node *maxPrefix, const char *num,
                                          size_t
                                          forwardingId);


const char **
removeDuplicates(const char **arrayOfStrings, size_t length, size_t *newLength);

bool sortAndRemoveDuplicatesFromPnum(PhoneNumbers *pNumbers);

void eraseSingleForwarding(Node *revTrie, Node *nodeFromTrie);

int getDigitId(char c);

char getCharByDigitId(int id);

/** Dealokuje pamięć z node i jego wewnętrznych struktur.
 * @param node - wskaźnik na wierzchołek
 */
void freeNode(Node *node);

PhoneForward *phfwdNew(void) {
    PhoneForward *pf;
    pf = malloc(sizeof(PhoneForward));

    if (pf == NULL)
        return NULL;

    pf->trie = getPtrToNewNode();

    if (pf->trie == NULL) {
        free(pf);
        return NULL;
    }

    pf->revTrie = getPtrToNewNode();

    if (pf->revTrie == NULL) {
        freeNode(pf->trie);
        free(pf);
        return NULL;
    }

    return pf;
}

void freeNode(Node *node) {
    if (node != NULL) {
        freeVector(node->forward);
        free(node);
    }
}

void phfwdDelete(PhoneForward *pf) {
    if (pf != NULL) {
        treeTraversal(pf->trie, DeleteNode, NULL);
        treeTraversal(pf->revTrie, DeleteNode, NULL);

        free(pf);
    }
}

Node *getNode(Node *actNode, const char *num) {
    while (isDigit(*num)) {
        int digit = getDigitId(*num);

        if (actNode->sons[digit] == NULL) {
            if (addSonToNode(actNode, digit) == false)
                return NULL;
        }

        actNode = actNode->sons[digit];
        num++;
    }

    return actNode;
}

bool phfwdAdd(PhoneForward *pf, const char *num1, const char *num2) {
    if (pf == NULL || !isCorrectNumber(num1) || !isCorrectNumber(num2) ||
        strcmp(num1, num2) == 0) {
        return false;
    }

    Node *from = getNode(pf->trie, num1);
    Node *to = getNode(pf->trie, num2);

    if (from == NULL || to == NULL)
        return false;

    setFirstPos(from->forward, to);

    from = getNode(pf->revTrie, num2);
    to = getNode(pf->revTrie, num1);

    if (from == NULL || to == NULL)
        return false;

    eraseByValue(from->forward, to);
    insert(from->forward, to);

    return true;
}
/** Zwraca przekierowany numer
 * @param pf - struktura przekierowań;
 * @param num - przekierowywane numer.
 * @return przekierowany numer lub NULL, gdy num jest NULL'em.
 */
const char *getForwardedNum(const PhoneForward *pf, const char *num) {
    if (num == NULL)
        return NULL;

    Node *maxPrefix = getMaxPrefixWithForwarding(pf->trie, num);

    return getForwardedNumWithMaxPrefix(maxPrefix,
                                        num);
}

PhoneNumbers *phfwdGet(const PhoneForward *pf, const char *num) {
    if (pf == NULL)
        return NULL;

    if (isCorrectNumber(num) == false)
        return getEmptyPnum();

    const char * forwardedNum = getForwardedNum(pf, num);

    if (forwardedNum == NULL)
        return NULL;

    PhoneNumbers *pnum = getSinglePhnum(forwardedNum);

    if (pnum == NULL)
        free((char *)forwardedNum);

    return pnum;
}

PhoneNumbers *getSinglePhnum(const char *num) {
    PhoneNumbers * phnum = getEmptyPnum();

    if (phnum == NULL)
        return NULL;

    if (pushBackPnum(phnum, num) == false) {
        phnumDelete(phnum);
        return NULL;
    }
    else {
        return phnum;
    }
}

void phfwdRemove(PhoneForward *pf, const char *num) {
    if (pf == NULL || !isCorrectNumber(num))
        return;

    Node *node = getNode(pf->trie, num);
    eraseForwardingsInTree(node, pf->revTrie);
}

char const *phnumGet(const PhoneNumbers *pnum, size_t idx) {
    if (pnum == NULL || idx >= pnum->total)
        return NULL;

    return pnum->numbers[idx];
}

void phnumDelete(PhoneNumbers *pnum) {
    if (pnum == NULL)
        return;

    for (size_t i = 0; i < pnum->total; i++)
        free((char *) pnum->numbers[i]);

    free(pnum->numbers);
    free(pnum);
}

PhoneNumbers *phfwdReverse(const PhoneForward *pf, const char *num) {
    if (pf == NULL)
        return NULL;

    if (!isCorrectNumber(num))
        return getEmptyPnum();

    Node *node = getNode(pf->revTrie, num);

    if (node == NULL)
        return NULL;

    PhoneNumbers *result = getEmptyPnum();

    if (result == NULL)
        return NULL;

    if (!pushBackPnum(result, strDuplicate(num))) {
        phnumDelete(result);

        return NULL;
    }

    while (!isRoot(node)) {
        const char * pastPrefix = getNum(node);
        if (pastPrefix == NULL) {
            phnumDelete(result);
            return NULL;
        }

        for (size_t i = 0; i < sizeOfAVector(node->forward); i++) {
            const char * prefixOfANewNum = getNum(getById(node->forward, i));

            char * newNum;
            if (prefixOfANewNum == NULL)
                newNum = NULL;
            else
                newNum = changePrefix(pastPrefix, prefixOfANewNum, num);

            if (!pushBackPnum(result, newNum)) {
                free((char *)prefixOfANewNum);
                free((char *)pastPrefix);

                phnumDelete(result);
                return NULL;
            }

            free((char *)prefixOfANewNum);
        }

        free((char *)pastPrefix);

        node = node->parent;
    }

    if (!sortAndRemoveDuplicatesFromPnum(result)) {
        phnumDelete(result);

        return NULL;
    }

    return result;
}

/** Filtruje pasujące numery.
 * @param pf - struktura przekierowań
 * @param pnum - numery, które będą filtrowane
 * @param fillInto - wskaźnik na strukturę, w której będą umieszczone pasujące
 * numery
 * @param num - numer na jaki pasujące numery są przekierowywane
 * @return czy doszło do błędu alokacji pamięci.
 */
bool filterMatchingForwarding(PhoneForward const *pf, PhoneNumbers *pnum,
                              PhoneNumbers *fillInto, const char *num) {
    bool failed = false;

    for (size_t i = 0; i < pnum->total; i++) {
        const char *candidateNum = phnumGet(pnum, i);
        const char *forwarded = getForwardedNum(pf, candidateNum);

        failed = candidateNum == NULL || forwarded == NULL;

        int strCmpRes = 1;
        if (!failed)
            strCmpRes = strcmp(forwarded, num);

        if (!failed && strCmpRes == 0)
            failed = !pushBackPnum(fillInto, strDuplicate(candidateNum));

        if (forwarded != NULL)
            free((char*)forwarded);

        if (failed)
            break;
    }

    if (failed) {
        phnumDelete(fillInto);
        return false;
    }

    return true;
}

PhoneNumbers *phfwdGetReverse(const PhoneForward *pf, const char *num) {
    PhoneNumbers *pnum = phfwdReverse(pf, num);
    if (pnum == NULL)
        return NULL;

    PhoneNumbers *result = getEmptyPnum();
    if (result == NULL) {
        phnumDelete(pnum);
        return NULL;
    }

    if (!filterMatchingForwarding(pf, pnum, result, num)) {
        phnumDelete(pnum);
        return NULL;
    }

    phnumDelete(pnum);

    return result;
}

/** Porównuje dwa numery telefonów.
 * @param a - numer telefonu
 * @param b - numer telefonu
 * @return Zwraca -1, 0 lub 1 gdy a jest odpowiednio mniejsze, równe, większe
 * od b.
 */
int compare(const void *a, const void *b) {
    char * strA = * (char * const *) a;
    char * strB = * (char * const *) b;

    while (!isEmptyNum(*strA) && !isEmptyNum(*strB)) {
        int idA = getDigitId(*strA);
        int idB = getDigitId(*strB);

        if (idA < idB)
            return -1;

        if (idA > idB)
            return 1;

        strA++;
        strB++;
    }

    if (*strA == *strB)
        return 0;

    if (isEmptyNum(*strA))
        return -1;
    else
        return 1;
}

/** Konwertuje znak na 'cyfrę' w numerze telefonu.
 * @param c - znak do konwersji
 * @return Zwraca liczbę, która odpowiada znakowi @p c lub -1 gdy nie ma
 * przypisanej do @p c liczby.
 */
int getDigitId(char c) {
    if ('0' <= c && c <= '9')
        return c - '0';
    if (c == '*')
        return 10;
    if (c == '#')
        return 11;

    return -1;
}

/** Sortuje i usuwa duplikaty numerów telefonów.
 * Zwraca posortowaną tablicę numerów telefonów z @p pnum, ale bez duplikatów.
 * @param pnum - numery telefonów
 * @return Zwraca czy udała się operacja w całości.
 */
bool sortAndRemoveDuplicatesFromPnum(PhoneNumbers *pnum) {
    qsort(pnum->numbers, pnum->total, sizeof(char *), compare);

    const char ** tmpNums = (const char **) removeDuplicates
            (pnum->numbers,
                                                      pnum->total,
                                                     &(pnum->total));
    if (tmpNums == NULL)
        return false;

    pnum->numbers = tmpNums;
    pnum->capacity = pnum->total;

    return true;
}

/** @brief Usuwa powtórzone numery.
 * Zwraca tablicę numerów telefonów, w której nie ma powtórzeń. Rozmiar nowej
 * tablicy jest zapisany w zmiennej @p newLength.
 * @param arrayOfStrings - tablica numerów
 * @param length - długość tablicy numerów
 * @param newLength - wskaźnik na zmienną, w której będzie zapisana nowa
 * długość tablicy
 * @return Zwraca tablicę numerów telefonów bez powtórzeń lub NULL jeśli
 * doszło do problemu z alokacją pamięci.
 */
const char **
removeDuplicates(const char **arrayOfStrings, size_t length, size_t *newLength) {
    size_t countRemoved = 0;

    for (size_t i = 1; i < length; i++)
        if (strcmp(arrayOfStrings[i - 1], arrayOfStrings[i]) == 0) {
            free((char *)arrayOfStrings[i - 1]);

            arrayOfStrings[i - 1] = NULL;

            countRemoved++;
        }

    *newLength = length - countRemoved;
    const char ** arrayWithoutDuplicates = malloc((*newLength) * sizeof
            (const char *));

    if (arrayWithoutDuplicates == NULL)
        return NULL;

    const char ** ptrToEmptyCell = arrayWithoutDuplicates;

    for (size_t i = 0; i < length; i++)
        if (arrayOfStrings[i] != NULL) {
            *ptrToEmptyCell = arrayOfStrings[i];
            ptrToEmptyCell++;
        }

    free(arrayOfStrings);

    return arrayWithoutDuplicates;
}

void functionOnNode(Node *node, FunctionType type, Node *revTrie) {
    if (node == NULL)
        return;

    if (type == RemoveForwarding) {
        if (sizeOfAVector(node->forward) > 0)
            eraseSingleForwarding(revTrie, node);

        erase(node->forward, 0);
    }
    else if (type == DeleteNode) {
        if (noSons(node))
            deleteNode(node);
    }
}

/** @brief Usuwa pojedyncze przekierowanie w drzewie @p revTrie.
 * Usuwa pojedyncze przekierowanie w drzewie @p revTrie. Usunięte
 * zostanie przekierowanie takie, że prowadzi z napisu reprezentującego @p
 * revTrie do napisu reprezentującgo @p nodeFromTrie.
 * @param revTrie - wskaźnik na korzeń drzewa revTrie
 * @param nodeFromTrie - wskaźnik na wierczchołek w drzewie trie, który
 * reprezentuje numer, do którego usuwamy przekierowanie.
 */
void eraseSingleForwarding(Node *revTrie, Node *nodeFromTrie) {
    const char * numFrom = getNum(nodeFromTrie);
    const char * numTo = getNum(getById(nodeFromTrie->forward, 0));

    Node * nodeFromInRevTrie = getNode(revTrie, numTo);
    Node * toErase = getNode(revTrie, numFrom);

    eraseByValue(nodeFromInRevTrie->forward, toErase);

    free((char *)numFrom);
    free((char *)numTo);
}

void deleteNode(Node *pNode) {
    int lastDigit = getDigitId(pNode->lastC);

    if (pNode->parent != NULL)
        pNode->parent->sons[lastDigit] = NULL;

    freeVector(pNode->forward);

    free(pNode);
}

bool noSons(Node *pNode) {
    for (int i = 0; i < NB_OF_SONS ; i++)
        if (pNode->sons[i] != NULL)
            return false;

    return true;
}
void
eraseForwardingsInTree(Node *nodeFromTrie, Node *revTrie) {
    treeTraversal(nodeFromTrie, RemoveForwarding, revTrie);
}

const char *getForwardedNumWithMaxPrefix(Node *maxPrefix, const char *num) {
    return getForwardedNumByForwardingId(maxPrefix, num, 0);
}

const char *strDuplicate(const char *num) {
    char * copy = malloc(strlen(num) + 1);

    if (copy == NULL)
        return NULL;

    strcpy(copy, num);

    return copy;
}

char * changePrefix(const char *pastPrefix, const char *newPrefix,
                    const char *string) {
    size_t pastPrefixLen = strlen(pastPrefix);

    size_t newLength = getNewNumLength(pastPrefixLen, strlen(newPrefix),
                                       strlen(string));

    char * newString = malloc(sizeof(char) * (newLength + 1));

    if (newString == NULL)
        return NULL;

    const char * newSuffix = string + pastPrefixLen;

    strcpy(newString, newPrefix);
    strcat(newString, newSuffix);

    return newString;
}

size_t getNewNumLength(size_t pastPrefixLen, size_t newPrefixLen, size_t
pastWordLen) {
    return pastWordLen - pastPrefixLen + newPrefixLen;
}

Node *getMaxPrefixWithForwarding(Node *pNode, const char *num) {
    Node *longestPrefix = NULL;

    while (pNode != NULL) {
        if (hasForwarding(pNode))
            longestPrefix = pNode;

        if (isDigit(*num)) {
            pNode = pNode->sons[getDigitId(*num)];
            num++;
            continue;
        }
        else {
            break;
        }
    }

    return longestPrefix;
}

bool isDigit(char c) {
    return ('0' <= c && c <= '9') || c == '*' || c == '#';
}

bool isEmptyNum(char c) {
    return c == '\0';
}

bool isCorrectNumber(char const *num) {
    if (num == NULL || isEmptyNum(*num))
        return false;

    while(isDigit(*num))
        num++;

    return *num == '\0';
}

char const *getNum(Node *pNode) {
    size_t length = pNode->depth;

    char * num = malloc(sizeof(char) * length);

    if (num == NULL)
        return NULL;

    getNumIntoArray(pNode, num, length);

    return num;
}

bool isRoot(Node *pNode) {
    return pNode->parent == NULL;
}

void getNumIntoArray(Node *pNode, char *num, size_t length) {
    num[length - 1] = '\0';
    length--;

    while (!isRoot(pNode)) {
        num[length - 1] = pNode->lastC;
        length--;

        pNode = pNode->parent;
    }
}

void treeTraversal(Node *tree, FunctionType type, Node *revTrie) {
    if (tree == NULL)
        return;

    Node *rootParent = tree->parent;

    while (tree != rootParent) {
        Node * next = nextNodeDFS(tree);

        functionOnNode(tree, type, revTrie);

        tree = next;
    }
}

bool resizePnum(PhoneNumbers *pnum) {
    char ** pnumTmpArr = realloc(pnum->numbers, pnum->capacity * 2 * sizeof
    (char *));

    if (pnumTmpArr == NULL)
        return false;

    pnum->numbers = (const char **) pnumTmpArr;
    pnum->capacity *= 2;

    return true;
}

PhoneNumbers *getEmptyPnum() {
    PhoneNumbers *pnum = malloc(sizeof(PhoneNumbers));

    if (pnum == NULL)
        return NULL;

    pnum->total = 0;
    pnum->capacity = 1;
    pnum->numbers = malloc(sizeof(char*));

    if (pnum->numbers == NULL) {
        phnumDelete(pnum);

        return NULL;
    }

    return pnum;
}

bool pushBackPnum(PhoneNumbers *pnums, const char *num) {
    if (!isCorrectNumber(num))
        return false;

    if (pnums->total == pnums->capacity) {
        if (resizePnum(pnums) == false) {
            free((char *)num);
            return false;
        }
    }

    (pnums->numbers)[pnums->total] = num;
    pnums->total++;

    return true;
}

Node *getPtrToNewNode() {
    Node *node = malloc(sizeof(Node));

    if (node == NULL)
        return NULL;

    node->parent = NULL;

    for(int i = 0; i < NB_OF_SONS; i++)
        node->sons[i] = NULL;

    node->forward = getEmptyVector();

    if (node->forward == NULL) {
        free(node);
        return NULL;
    }

    node->depth = 1;
    node->lastC = '\0';
    node->nxtInTreeTraversal = 0;

    return node;
}

bool hasForwarding(Node *pNode) {
    return sizeOfAVector(pNode->forward) > 0;
}

/** Konwertuje liczbę @p id na znak ją reprezentujący.
 * @param id - liczba do konwersji
 * @return Zwraca konwersję @p id lub '\0', gdy nie udała się konwersja.
 */
char getCharByDigitId(int id) {
    if (0 <= id && id <= 9)
        return id + '0';
    if (id == 10)
        return '*';
    if (id == 11)
        return '#';

    return '\0';
}

bool addSonToNode(Node *node, int sonId) {
    node->sons[sonId] = getPtrToNewNode();

    if (node->sons[sonId] == NULL)
        return false;

    node->sons[sonId]->parent = node;
    node->sons[sonId]->depth = node->depth + 1;
    node->sons[sonId]->lastC = getCharByDigitId(sonId);

    return true;
}

int getNextNodeDFSId(int pastId) {
    int newId = pastId + 1;

    if (newId == NB_OF_SONS)
        newId = -1;

    return newId;
}

Node *nextNodeDFS(Node *node) {
    int nextId = node->nxtInTreeTraversal;

    if (nextId == -1) {
        node->nxtInTreeTraversal = 0;

        return node->parent;
    }
    else {
        while (node->sons[node->nxtInTreeTraversal] == NULL) {
            int actId = node->nxtInTreeTraversal;
            int nxtId = getNextNodeDFSId(actId);

            node->nxtInTreeTraversal = nxtId;

            if (nxtId == -1) {
                node->nxtInTreeTraversal = 0;
                return node->parent;
            }
        }
        int actId = node->nxtInTreeTraversal;
        node->nxtInTreeTraversal = getNextNodeDFSId(actId);

        return node->sons[actId];
    }
}

/** @brief Tworzy numer telefonu, używając i-tego przekierowania.
 * Tworzy numer telefonu używając przekierowania numer @p forwardingId.
 * @param maxPrefix - wierzchołek z którego idzie przekierowanie
 * @param num - przekierowywany numer
 * @param forwardingId - numer przekierowania z wierzchołka maxPrefix
 * @return Wskaźnik na przekierowany numer telefonu lub NULL gdy doszło do
 * problemu z alokacją pamięci. W przypadku @p maxPrefix równego NULL, zwraca
 * kopię @p num.
 */
const char *getForwardedNumByForwardingId(Node *maxPrefix, const char *num,
                                          size_t forwardingId) {
    if (maxPrefix == NULL)
        return strDuplicate(num);

    const char * pastPrefix = getNum(maxPrefix);

    if (pastPrefix == NULL)
        return NULL;

    const char * newPrefix = getNum(getById(maxPrefix->forward, forwardingId));

    if (newPrefix == NULL) {
        free((char *)pastPrefix);
        return NULL;
    }

    char * newNum = changePrefix(pastPrefix, newPrefix, num);

    free((char *)pastPrefix);
    free((char *)newPrefix);

    return newNum;
}
