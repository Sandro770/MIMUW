CC=gcc
CFLAGS=-Wall -Wextra -Wno-implicit-fallthrough -std=c17 -O2
OBJS=shortest-path.o input.o errors.o vector.o labyrinth.o

.PHONY: all clean

all: labyrinth

labyrinth: $(OBJS)
shortest-path.o: shortest-path.h definitions.h input.h vector.h errors.h
labyrinth.o: input.h shortest-path.h
errors.o: errors.h
vector.o: vector.h definitions.h errors.h
input.o: input.h errors.h definitions.h

clean:
	-rm labyrinth $(OBJS)
