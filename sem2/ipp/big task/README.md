# Phone numbers - big task

This module that I implemented for Individual Programming Project course at University of Warsaw.
The signatures and documentation of functions in phone_forward, some of the Cmake commands and doxygen file was implemented by this course coordinator.

This module's goal was to handle creating phone forwardings: creating them, forwarding a phone number, doing the reverse - creating the list of phone numbers that it's forwardings are a particular phone number.

## Requirements
Doxygen, Cmake, C compiler

## How to run (linux)

### release
mkdir release <br />
cd release <br />
cmake .. <br />
make <br />
make doc <br />

### debug
mkdir debug <br />
cd debug <br />
cmake -D CMAKE_BUILD_TYPE=Debug .. <br />
make <br />
make doc <br />
