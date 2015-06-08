#ifndef RADIXPATRICIA_HPP_INCLUDED
#define RADIXPATRICIA_HPP_INCLUDED

#include "Node.hpp"
#include <vector>

/**
 * @author Spas Kyuchukov
 */

class RadixPatricia {

private:
    Node * _root;
    std::vector<char *> dictionary;

    Node * findNode(const char* prefix, bool exact_match);
    static void recursiveAddValue(Node * node, std::vector<int>& vec);

public:
	RadixPatricia();
	~RadixPatricia();

	// adds a word with it's corresponding data to the tree
	void insert(const char* word, int data);

	// gets the data for a word if it is contained in the tree
	// else returns a number < 0
	int find(const char* word);

	// returns a vector with all words in the tree starting with prefix
	std::vector<int> getAllWithPrefix(const char* prefix);

	// removes a word from the tree, returns true on success
	// (there was a word like that and it was removed) and false otherwise
	bool remove(const char* word);
};

#endif // RADIXPATRICIA_HPP_INCLUDED
