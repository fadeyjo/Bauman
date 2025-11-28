CXX = c++
CXXFLAGS = -Wall -Wextra -std=c++17
TARGETS = client server

all: $(TARGETS)

client: client.cpp
	$(CXX) $(CXXFLAGS) -o client client.cpp

server: server.cpp
	$(CXX) $(CXXFLAGS) -o server server.cpp

clean:
	rm -f $(TARGETS) *.o results.txt

.PHONY: all clean
