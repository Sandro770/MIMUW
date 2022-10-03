/* author: Alessandro Peria */

#include "shortest-path.h"
#include "input.h"

int main() {
    PInput inputPtr;
    initializeInput(&inputPtr);

    bool isValidInput = readInput(inputPtr);

    long shortestPath;
    if (isValidInput)
        shortestPath = findShortestPath(inputPtr);

    freeMemory(inputPtr);

    if (isValidInput)
        printShortestPath(shortestPath);
    else
        return 1;

    return 0;
}