#ifdef NDEBUG
#undef NDEBUG
#endif

#include "phone_forward.h"
#include <assert.h>
#include <string.h>
#include <stdio.h>

#define MAX_LEN 23

#define GREEN   "\033[0;32m"
#define RESET_COLOR "\033[0m"

void phfwdAddTests();

void phfwdGetTests();

int testId = 1;

void printTestPassed() {
    printf("Test %d: ", testId++);
    printf("%sPASSED%s\n", GREEN, RESET_COLOR);
}
void printGreeting() {
    printf("Testy rozpoczęte\n\n");
}

int main(void) {

    printGreeting();

    PhoneForward *pf;
    PhoneNumbers *pnum;


    /* phwdNew */
    printf("Testowanie phfwdNew\n");
    pf = phfwdNew();
    printTestPassed();
    phfwdDelete(pf);


    /* phfwdDelete */
    printf("Testowanie phfwdDelete: \n");

    /* zwykłe usunięcie struktury */
    pf = phfwdNew();
    phfwdDelete(pf);
    printTestPassed();

    pf = NULL;
    phfwdDelete(pf);
    printTestPassed();

    phfwdAddTests();

    phfwdGetTests();

    /* phnumDelete */
    printf("Testowanie phnumDelete\n");
    phnumDelete(NULL);
    printTestPassed();


    /* phnumGet */
    printf("Testowanie phnumGet\n");
    assert(phnumGet(NULL, 0) == NULL);
    printTestPassed();



    /* BIG COMPLEX EXAMPLE*/
    printf("Testowanie złożonego przykładu\n");
    pf = phfwdNew();

    assert(true == phfwdAdd(pf, "11", "113"));
    printTestPassed();

    pnum = phfwdGet(pf, "114");
    assert(strcmp(phnumGet(pnum, 0), "1134") == 0);
    phnumDelete(pnum);

    assert(phfwdAdd(pf, "119", "1"));
    printTestPassed();

    assert(phfwdAdd(pf, "113", "19"));
    printTestPassed();

    assert(phfwdAdd(pf, "1197", "191"));
    printTestPassed();

    pnum = phfwdGet(pf, "1198");
    assert(strcmp(phnumGet(pnum, 0), "18") == 0);
    phnumDelete(pnum);
    printTestPassed();

    pnum = phfwdGet(pf, "11");
    assert(strcmp(phnumGet(pnum, 0), "113") == 0);
    phnumDelete(pnum);
    printTestPassed();

    phfwdRemove(pf, "119");
    pnum = phfwdGet(pf, "1197");
    assert(strcmp(phnumGet(pnum, 0), "11397") == 0);
    phnumDelete(pnum);
    printTestPassed();

    phfwdDelete(pf);
}

void phfwdGetTests() {
    printf("Testowanie phfwdGet \n");

    char num[MAX_LEN + 1];
    PhoneForward *pf;
    PhoneNumbers *pnum;

    /* num nie jest numerem, powinno zwrócić pusty ciąg */
    pf = phfwdNew();
    pnum = phfwdGet(pf, "");
    assert(pnum != NULL && phnumGet(pnum, 0) == NULL);
    phnumDelete(pnum);
    phfwdDelete(pf);
    printTestPassed();

    /* pf == NULL */
    strcpy(num, "312");
    assert(NULL == phfwdGet(NULL, num));
    printTestPassed();

    /* pf == NULL, num == NULL i czy na koniec pusty ciąg */
    pnum = phfwdGet(NULL, NULL);
    assert(NULL == pnum);
    printTestPassed();

    /* pf == coś, num == NULL i czy na koniec pusty ciąg */
    pf = phfwdNew();
    pnum = phfwdGet(pf, NULL);
    assert(NULL != pnum);
    printTestPassed();
    assert(phnumGet(pnum, 0) == NULL);
    phfwdDelete(pf);
    phnumDelete(pnum);
    printTestPassed();

    /* proste przekierowanie*/
    pf = phfwdNew();
    phfwdAdd(pf, "123", "1");
    pnum = phfwdGet(pf, "123123");
    assert(strcmp("1123", phnumGet(pnum, 0)) == 0);
    phnumDelete(pnum);
    printTestPassed();
    /* bez przekierowania */
    pnum = phfwdGet(pf, "12089");
    assert(strcmp("12089", phnumGet(pnum, 0)) == 0);
    printTestPassed();
    /* czy tylko jeden numer był w pnum*/
    assert(phnumGet(pnum, 1) == NULL);
    phfwdDelete(pf);
    phnumDelete(pnum);
    printTestPassed();

}

void phfwdAddTests() {
    printf("Testowanie phfwdAdd \n");

    char num1[MAX_LEN + 1], num2[MAX_LEN + 1];
    PhoneForward *pf;

    /* takie same numery niepuste*/
    pf = phfwdNew();
    strcpy(num1, "23543");
    strcpy(num2, "23543");
    assert(false == phfwdAdd(pf, num1, num2));
    printTestPassed();
    phfwdDelete(pf);

    /* takie same numery puste */
    pf = phfwdNew();
    strcpy(num1, "");
    strcpy(num2, "");
    assert(false == phfwdAdd(pf, num1, num2));
    printTestPassed();
    phfwdDelete(pf);

    /* oba puste */
    pf = phfwdNew();
    strcpy(num1, "");
    strcpy(num2, "");
    assert(false == phfwdAdd(pf, num1, num2));
    printTestPassed();
    phfwdDelete(pf);

    /* pusty niepusty -> powinno byc false bo nie numer*/
    pf = phfwdNew();
    strcpy(num1, "");
    strcpy(num2, "01239876543");
    assert(false == phfwdAdd(pf, num1, num2));
    printTestPassed();
    phfwdDelete(pf);

    /* numer nie numer */
    pf = phfwdNew();
    strcpy(num1, "123");
    strcpy(num2, "92309-1");
    assert(false == phfwdAdd(pf, num1, num2));
    printTestPassed();
    phfwdDelete(pf);

    /* pf == NULL */
    pf = NULL;
    strcpy(num1, "123");
    strcpy(num2, "3214");
    assert(false == phfwdAdd(pf, num1, num2));
    printTestPassed();
    phfwdDelete(pf);

    /* numer w nie numer */
    pf = phfwdNew();
    strcpy(num1, "123");
    strcpy(num2, "");
    assert(false == phfwdAdd(pf, num1, num2));
    printTestPassed();
    phfwdDelete(pf);

    /* prawidłowe */
    pf = phfwdNew();
    strcpy(num1, "123");
    strcpy(num2, "1234");
    assert(true == phfwdAdd(pf, num1, num2));
    printTestPassed();
    phfwdDelete(pf);

    /* prawidłowe */
    pf = phfwdNew();
    strcpy(num1, "9");
    strcpy(num2, "0");
    assert(true == phfwdAdd(pf, num1, num2));
    printTestPassed();
    phfwdDelete(pf);

    /* num1 = NULL */
    pf = phfwdNew();
    strcpy(num2, "0");
    assert(false == phfwdAdd(pf, NULL, num2));
    printTestPassed();
    phfwdDelete(pf);

    /* num2 = NULL */
    pf = phfwdNew();
    strcpy(num1, "0");
    assert(false == phfwdAdd(pf, num1, NULL));
    printTestPassed();
    phfwdDelete(pf);
}

