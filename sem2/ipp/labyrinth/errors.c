#include <stdlib.h>
#include <stdio.h>

#include "errors.h"

void memoryError() {
    inputError(0);
    exit(1);
}

void inputError(int lineNumber) {
    fprintf(stderr, "ERROR %d\n", lineNumber);
}