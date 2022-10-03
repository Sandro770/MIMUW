#define  _GNU_SOURCE
#include <errno.h>
#include <stdio.h>
#include <ctype.h>
#include <string.h>

#include "input.h"
#include "errors.h"

#define N 1
#define X 2
#define Y 3
#define VISITED 4
#define NONE 0

struct Formula{
    uint32_t a;
    uint32_t b;
    uint32_t m;
    uint32_t r;
    uint32_t s0;
};

typedef struct Formula *PFormula;


bool isEmptyStartAndBeginning(PInput pInput);

ul myLog2(ul i);

bool areCorrectCharactersInInputLine(char *linePtr, bool isHexBase);

bool isCorrectCharacter(char c, bool isHexBase);

int getAmountOfWordsInLine(char * linePtr);

bool splitNumbersInLine(char *linePtr, ul *howMany, ul **numbers);

bool readLine(char **line);

bool areCoordinatesInsideLabyrinth(ul *cords, PInput pInput);

bool isEndOfALine(const char *line);

bool isRInLine(char *line);

bool amountOfWordsAsExpected(char *line, ul expectedNumberOfWords);

bool isEmptyLine(char *line);

void skipWhiteSpaces(char **line);

bool isCorrectPrefix(char *line, bool isHex);

void skipPrefix(char **line, bool isHex);

bool transferNumbersFromArray(const ul *array, PFormula f);

bool get5Numbers(char *line, PFormula f);

bool read5uint32_t(char *line, PFormula f);

void generateW(PFormula f, ul nProduct, uint32_t *w);

void changeSIntoW(uint32_t *s, uint32_t lengthOfS, ul nProduct);

void generateS(uint32_t *s, PFormula f);

void generateVisitedFromFormula(PInput pInput, PFormula pFormula);

void generateVisitedFromW(uint32_t *w, PInput pInput, uint32_t wLength);

ul min(ul a, ul b);

bool convertFromFormulaToVisited(PInput pInput, char *line);

void setBitInVisited(PInput pInput, ul bitNumber);

void generateVisitedFromHex(PInput pInput, char *line);

bool validateNumberOfBitsInHex(char *line, PInput pInput);

bool validateIfIsOnlyPrefixInHex(char *line);

bool preValidateFourthLineHex(char *line, PInput pInput);

bool convertLineIntoVisited(PInput pInput, char *line);

void setVisitedSize(PInput pInput);

bool noMoreLines();

int getDecFromHex(char i);

void initializeVisitedArray(PInput pInput);

bool checkDimensions(PInput pInput);

bool read(PInput pInput, int lineNumber);

bool readAndConvertLine(PInput pInput, int lineNumber);

bool convertLine(PInput pInput, char *line, int lineNumber);

bool preValidate(PInput pInput, char *line, int lineNumber);

bool preValidateNXY(char *line, PInput pInput);

bool preValidateVisited(char *line, PInput pInput);

bool transform(PInput pInput, char *line, int lineNumber);

bool postValidate(PInput pInput, int lineNumber);

void initializeVisited(PVisited *pVisited);

bool isEndOfAWord(char *linePtr);

void freeMemory(PInput pInput) {
    free(pInput->n);
    free(pInput->x);
    free(pInput->y);
    free(pInput->pVisited->array);
    free(pInput->pVisited);
    free(pInput);
}

bool wasVisited(PInput pInput, ul vertex) {
    ul id = getIndexInVisited(vertex);
    uint32_t bit = getBitInVisited(vertex);

    return ((pInput->pVisited->array[id] & bit) != 0);
}

bool areCorrectCharactersInInputLine(char *linePtr, bool isHexBase) {
    while (isEndOfALine(linePtr) == false) {
        if (isCorrectCharacter(*linePtr, isHexBase))
            linePtr++;
        else
            return false;
    }

    return true;
}

bool isCorrectCharacter(char c, bool isHexBase) {
    if (isspace(c) || ('0' <= c && c <= '9'))
        return true;

    return isHexBase && (
           ('a' <= c && c <= 'f') ||
           ('A' <= c && c <= 'F')
           );
}

int getAmountOfWordsInLine(char * linePtr) {
    int amountOfNumbers = 0;

    for (; isEndOfALine(linePtr) == false; linePtr++)
        if (isEndOfAWord(linePtr))
            amountOfNumbers++;

    return amountOfNumbers;
}

bool isEndOfAWord(char *linePtr) {
    char * next = linePtr + 1;
    return !isspace(*linePtr) &&
           (isspace(*next) || isEndOfALine(next));
}

void initializeInput(PInput *pInput) {
    *pInput = malloc(sizeof(struct Input));

    if (*pInput == NULL)
        memoryError();

    (*pInput)->n = NULL;
    (*pInput)->x = NULL;
    (*pInput)->y = NULL;
    (*pInput)->k = NONE;

    initializeVisited(&((*pInput)->pVisited));
}

void initializeVisited(PVisited *pVisited) {
    *pVisited = malloc(sizeof(struct Visited));
    if ((*pVisited) == NULL)
        memoryError();

    (*pVisited)->array = NULL;
    (*pVisited)->length = 0;
}

bool splitNumbersInLine(char *linePtr, ul *howMany, ul **numbers) {
    ul amountOfWords = getAmountOfWordsInLine(linePtr);

    *numbers = malloc(amountOfWords * sizeof(ul));

    if (*numbers == NULL)
        memoryError();

    if (howMany != NULL)
        *howMany = amountOfWords;

    int idInResultArray = 0;

    while (1) {
        char *previousPtr = linePtr;
        ul nb = strtoul(linePtr, &linePtr, 10);

        if (errno == ERANGE)
            return false;

        if (previousPtr == linePtr) {
            if (*linePtr == '0') /// if *linePtr is a leading zero
                linePtr++;
            else
                break;
        }

        (*numbers)[idInResultArray] = nb;
        idInResultArray++;
    }

    return true;
}

bool readLine(char **line) {
    size_t length = 0;

    return -1 != getline(line, &length, stdin);
}

bool areCoordinatesInsideLabyrinth(ul *cords, PInput pInput) {
    for (ul i = 0; i < pInput->k; i++)
        if (cords[i] > pInput->n[i] || cords[i] < 1)
            return false;

    return true;
}

bool checkDimensions(PInput pInput) {
    for (ul i = 0; i < pInput->k; i++)
        if ((pInput->n)[i] == 0)
            return false;

    return true;
}

bool isEndOfALine(const char *line) {
    return *line == '\n' || *line == EOF || *line == '\0';
}

bool isRInLine(char *line) {
    return strchr(line, 'R') != NULL;
}

bool amountOfWordsAsExpected(char *line, ul expectedNumberOfWords) {
    ul actualAmount = getAmountOfWordsInLine(line);

    return expectedNumberOfWords == NONE ||
           actualAmount == expectedNumberOfWords;
}

bool isEmptyLine(char *line) {
    skipWhiteSpaces(&line);

    return isEndOfALine(line);
}

void skipWhiteSpaces(char **line) {
    while (isEndOfALine(*line) == false) {
        if (isspace(**line))
            (*line)++;
        else
            break;
    }
}

void getPrefix(char *line, char *a, char *b) {
    skipWhiteSpaces(&line);

    *a = *line;

    if (isEndOfALine(line))
        *b = EOF;
    else
        *b = *(line + 1);
}

bool isCorrectPrefix(char *line, bool isHex) {
    char c0, c1;

    getPrefix(line, &c0, &c1);

    if (isHex)
        return c0 == '0' && c1 == 'x';
    else
        return c0 == 'R';
}

void skipPrefix(char **line, bool isHex) {
    skipWhiteSpaces(line);

    if (isHex)
        *line += 2;
    else
        (*line)++;
}

bool preValidateFourthLine(char * line, ul expNbOfWords, bool isHex) {
    if (isCorrectPrefix(line, isHex)) {
        skipPrefix(&line, isHex);

        return areCorrectCharactersInInputLine(line, isHex) &&
               amountOfWordsAsExpected(line, expNbOfWords);
    }
    else {
        return false;
    }
}

bool transferNumbersFromArray(const ul *array, PFormula f) {
    for (int i = 0; i < 5; i++)
        if (array[i] > UINT32_MAX)
            return false;

    f->a = array[0];
    f->b = array[1];
    f->m = array[2];
    f->r = array[3];
    f->s0 = array[4];

    return f->m != 0;
}

bool get5Numbers(char *line, PFormula f) {
    ul *numbersArray;

    bool noErrors =
            splitNumbersInLine(line, NULL, &numbersArray) &&
            transferNumbersFromArray(numbersArray, f);

    free(numbersArray);

    return noErrors;
}

bool read5uint32_t(char *line, PFormula f) {
    skipPrefix(&line, false);

    return get5Numbers(line, f);
}

void initializeFormula(PFormula f) {
    f->a = 0;
    f->b = 0;
    f->m = 0;
    f->r = 0;
    f->s0 = 0;
}

void generateW(PFormula f, ul nProduct, uint32_t *w) {
    generateS(w, f);
    changeSIntoW(w, f->r, nProduct);
}

void changeSIntoW(uint32_t *s, uint32_t lengthOfS, ul nProduct) {
    if (nProduct > (ul)1<<(ul)32)
        nProduct = (ul)1<<(ul)32;

    for (uint32_t i = 0; i < lengthOfS; i++)
        s[i] %= nProduct;
}

void generateS(uint32_t *s, PFormula f) {
    for(uint32_t i = 0; i < f->r; i++) {
        if (i == 0)
            s[i] = ((uint64_t)f->a * (uint64_t )f->s0) % f->m;
        else
            s[i] = ((uint64_t)f->a * (uint64_t )s[i - 1]) % f->m;
        s[i] = ((uint64_t)s[i] + (uint64_t)f->b) % f->m;
    }

}

void generateVisitedFromFormula(PInput pInput, PFormula pFormula) {
    uint32_t *w = malloc(pFormula->r * sizeof(uint32_t));

    if (w == NULL)
        memoryError();

    ul nProduct = getNProduct(pInput);

    generateW(pFormula, nProduct, w);
    generateVisitedFromW(w, pInput, pFormula->r);

    free(w);
}

void generateVisitedFromW(uint32_t *w, PInput pInput, uint32_t wLength) {
    ul nProduct = min((ul)1<<(ul)32, getNProduct(pInput));

    int32_t *exists;

    size_t sz = (nProduct + 31) / 32;
    exists = malloc(sizeof(int32_t) * sz);

    if (exists == NULL)
        memoryError();

    for (ul i = 0; i < sz; i++)
        exists[i] = false;

    for (ul i = 0; i < wLength; i++) {
        ul id = getIndexInVisited(w[i]);
        uint32_t bit = getBitInVisited(w[i]);
        exists[id] |= bit;
    }

    for (ul j = 0; j < 32LU * (pInput->pVisited->length); j++) {
        ul idExists = getIndexInVisited(j % nProduct);
        uint32_t bitExists = getBitInVisited(j % nProduct);

        if ((exists[idExists] & bitExists) != 0) {
            ul id = getIndexInVisited(j);
            uint32_t bit = getBitInVisited(j);

            pInput->pVisited->array[id] |= bit;
        }
    }
    free(exists);
}

ul min(ul a, ul b) {
    return a < b ? a : b;
}

uint32_t getBitInVisited(ul j) {
    ul bitNb = j % 32;

    return (1 << bitNb);
}

ul getIndexInVisited(ul j) {
    return j / (ul)32;
}

ul getNProduct(PInput pInput) {
    uint64_t product = 1;

    for (ul i = 0; i < pInput->k; i++) {
        ul ni = pInput->n[i];

        if (product >= SIZE_MAX / ni)
            memoryError();

        product = product * ni;
    }

    return product;
}

bool convertFromFormulaToVisited(PInput pInput, char *line) {
    struct Formula f;
    initializeFormula(&f);

    if (read5uint32_t(line, &f) == false)
        return false;

    generateVisitedFromFormula(pInput, &f);

    return true;
}

void setBitInVisited(PInput pInput, ul bitNumber) {
    ul id = getIndexInVisited(bitNumber);
    uint32_t bit = getBitInVisited(bitNumber);

    pInput->pVisited->array[id] |= bit;
}

int getDecFromHex(char i) {
    if ('0' <= i && i <= '9')
        return i - '0';
    else if ('a' <= i && i <= 'f')
        return i - 'a' + 10;
    else
        return i - 'A' + 10;
}

void generateVisitedFromHex(PInput pInput, char *line) {
    skipPrefix(&line, true);

    char * beginningOfAline = line;

    while (false == isEndOfAWord(line))
        line++;

    ul bitNumber = 0;
    while (true) {
        for (int i = 0; i < 4; i++)
            if ((getDecFromHex(*line) & (1 << i)) != 0)
                setBitInVisited(pInput, bitNumber + i);

        bitNumber += 4;

        if (line == beginningOfAline)
            break;

        line--;
    }
}

ul myLog2(ul i) {
    ul cnt = 0;

    while (i / (ul)2 > (ul)0) {
        cnt++;
        i /= (ul)2;
    }

    return cnt;
}

bool validateNumberOfBitsInHex(char *line, PInput pInput) {
    skipPrefix(&line, true);

    while (*line == '0')
        line++;

    if (isEndOfALine(line) || isspace(*line))
        return true;

    ul bitNumberInBinary = myLog2(*line - '0');
    line++;

    while (isspace(*line) == false && isEndOfALine(line) == false) {
        bitNumberInBinary += 4;
        line++;
    }

    return bitNumberInBinary < getNProduct(pInput);
}

bool validateIfIsOnlyPrefixInHex(char *line) {
    skipPrefix(&line, true);

    return false == isspace(*line) && false == isEndOfALine(line);
}

bool preValidateFourthLineHex(char *line, PInput pInput) {
    return preValidateFourthLine(line, 1, true) &&
           validateIfIsOnlyPrefixInHex(line) &&
           validateNumberOfBitsInHex(line, pInput);
}

bool convertLineIntoVisited(PInput pInput, char *line) {
    setVisitedSize(pInput);
    initializeVisitedArray(pInput);

    if (isRInLine(line))
        return convertFromFormulaToVisited(pInput, line);
    else {
        generateVisitedFromHex(pInput, line);
        return true;
    }
}

void setVisitedSize(PInput pInput) {
    ul product = getNProduct(pInput);
    ul arrSize = (product + (ul)31) / (ul)32;

    pInput->pVisited->array = malloc(arrSize * sizeof(uint32_t));
    pInput->pVisited->length = arrSize;

    if (pInput->pVisited->array == NULL)
        memoryError();
}

void initializeVisitedArray(PInput pInput) {
    size_t length = (pInput->pVisited->length) * sizeof(uint32_t);
    memset((pInput->pVisited->array), 0, length);
}

bool noMoreLines() {
    if (getc(stdin) == EOF)
        return true;

    inputError(5);

    return false;
}

ul cordsToNumber(PInput pInput, ul *cords) {
    ul number = 0;
    ul product = 1;

    for (ul i = 0; i < pInput->k; i++){
        number += product * (cords[i] - 1UL);
        product *= pInput->n[i];
    }

    return number;
}

bool isEmptyStartAndBeginning(PInput pInput) {
    ul start = cordsToNumber(pInput, pInput->x);
    ul destination = cordsToNumber(pInput, pInput->y);

    if (wasVisited(pInput, start)) {
        inputError(2);

        return false;
    }
    else if (wasVisited(pInput, destination)) {
        inputError(3);

        return false;
    }
    else {
        return true;
    }
}

bool preValidateNXY(char *line, PInput pInput) {
    return areCorrectCharactersInInputLine(line, false) &&
           amountOfWordsAsExpected(line, pInput->k);
}

bool preValidateVisited(char *line, PInput pInput) {
    if (isRInLine(line))
        return preValidateFourthLine(line, 5, false);
    else
        return preValidateFourthLineHex(line, pInput);
}

bool preValidate(PInput pInput, char *line, int lineNumber) {
    if (isEmptyLine(line))
        return false;

    if (1 <= lineNumber && lineNumber <= 3)
        return preValidateNXY(line, pInput);
    else
        return preValidateVisited(line, pInput);
}

bool transform(PInput pInput, char *line, int lineNumber) {
    if (lineNumber == N)
        return splitNumbersInLine(line, &(pInput->k), &(pInput->n));
    else if (lineNumber == X)
        return splitNumbersInLine(line, NULL, &(pInput->x));
    else if (lineNumber == Y)
        return splitNumbersInLine(line, NULL, &(pInput->y));
    else
        return convertLineIntoVisited(pInput, line);
}

bool convertLine(PInput pInput, char *line, int lineNumber) {
    return preValidate(pInput, line, lineNumber) &&
           transform(pInput, line, lineNumber);
}

bool readAndConvertLine(PInput pInput, int lineNumber) {
    char * line = NULL;

    bool noErrors = readLine(&line) &&
                    convertLine(pInput, line, lineNumber);

    free(line);

    return noErrors;
}

bool postValidate(PInput pInput, int lineNumber) {
    if (lineNumber == N)
        return checkDimensions(pInput);
    else if (lineNumber == X)
        return areCoordinatesInsideLabyrinth(pInput->x, pInput);
    else if (lineNumber == Y)
        return areCoordinatesInsideLabyrinth(pInput->y, pInput);
    else
        return true;
}

bool read(PInput pInput, int lineNumber) {
    bool noErrors = readAndConvertLine(pInput, lineNumber) &&
                    postValidate(pInput, lineNumber);

    /* it's separate case, because we want
     * to print ERROR 2/3 instead of ERROR 4 */
    if (noErrors && lineNumber == VISITED)
        return isEmptyStartAndBeginning(pInput);

    if (noErrors == false)
        inputError(lineNumber);

    return noErrors;
}

bool readInput(PInput pInput) {
    return read(pInput, N) &&
           read(pInput, X) &&
           read(pInput, Y) &&
           read(pInput, VISITED) &&
           noMoreLines();
}