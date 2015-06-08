/*
 * RadixPatricia.cpp
 *
 *  Created on: May 19, 2015
 *      Author: Spas Kyuchukov
 */

#include "RadixPatricia.hpp"
#include <iostream>
#include <sstream>
#include <string.h>
#include <string>
#include <assert.h>

// Assuming a byte is 8 bits
const int UNSIGNED_SHORT_SIZE_BITS = (sizeof(unsigned short) * 8);

RadixPatricia::RadixPatricia() : _root(new Node()){}

RadixPatricia::~RadixPatricia()
{
	if(_root != nullptr)
    {
        delete _root;
    }

    //std::cout<<" vector size: "<<dictionary.size()<<"; capacity: "<<dictionary.capacity()<<std::endl;

    for(char * word : dictionary)
    {
        delete [] word;
    }

    dictionary.clear();
    dictionary.shrink_to_fit();

}

// adds a word with it's corresponding data to the tree
void RadixPatricia::insert(const char* word, int data)
{
    // Bad input, not cool
    if(word == nullptr || word == NULL || data < 0)
    {
        return;
    }

    long long symbol_count = (long long) strlen(word);

    // Empty string is recognised!
    if(symbol_count <= 0)
    {
        _root->_value = data;
    }

    // Move semantics would have been perfect but oh those raw char* C-style pointers
    char * copied_word = new char[symbol_count+1];
    strcpy(copied_word, word);

    dictionary.push_back(copied_word);

    // total number of bits copied_word is long
    long long bit_count = symbol_count * CHAR_SIZE_BITS;
    // current bit index with respect to copied_word
    long long bit_idx = 0;
    // offset (with respect to copied_word) at wich curr_edge starts
    long long bit_offset = 0;

    Node * curr_node = _root;
    // the value of the first bit of copied_word
    bool bit_val = getBit(copied_word, bit_idx);
    //bool edge_bit_val;

    // If there is no edge to start from (the first 0/1 doesn't match)
    if(curr_node->_word_ptr[bit_val] == nullptr)
    {
        // add new Edge with copied_word and put it's value in the corresponding node at the end
        curr_node->addLongEdge(bit_val, copied_word, bit_idx, bit_count - bit_idx, data); // also makes _end_node !
        return;
    }
    // get the starting edge
    bool curr_edge = bit_val;
    ++bit_idx;

    while (bit_idx < bit_count)
    {
        // The index relative to the current edge's beginning index (is computed, not explicitly stored)
        int edge_relative_idx = bit_idx - bit_offset;

        // We are still inside the current_edge
        if(edge_relative_idx < curr_node->_length[curr_edge])
        {
            // There was a check for more bits at a time here, but
            // char by char was slower than bit by bit
            // and more than 8 bits at a time was architecture specific and buggy

            bit_val = getBit(copied_word, bit_idx);
            bool edge_bit_val = getBit(curr_node->_word_ptr[curr_edge], bit_idx);

            // If different bit is found we split with new node
            if(bit_val != edge_bit_val)
            {
                // Save the original end_bit and node at the end
                unsigned char old_length = curr_node->_length[curr_edge];
                Node * old_node = curr_node->_end_node[curr_edge];
                // (bit_idx - bit_offset) is > 1 if there is a difference
                unsigned char end_length = old_length - (bit_idx - bit_offset);
                unsigned char middle_length = old_length - end_length;
                curr_node->_length[curr_edge] = middle_length;
                // new Node to be splitter
                curr_node->_end_node[curr_edge] = new Node();
                // attach the rest of the old sequence as a new edge
                curr_node->_end_node[curr_edge]->addShortEdge(
                    edge_bit_val, curr_node->_word_ptr[curr_edge], end_length);
                // just shorten the calls downards
                curr_node = curr_node->_end_node[curr_edge];
                // attach the original node at the end of the old sequence
                curr_node->_end_node[edge_bit_val] = old_node;
                // attach the rest of the copied_word and add it's value to the terminating node
                curr_node->addLongEdge(bit_val, copied_word, bit_idx, bit_count - bit_idx, data);
                return;
            }

            ++bit_idx;
        }
        else // End of curr_edge was reached
        {
            bit_offset += curr_node->_length[curr_edge];
            if(bit_idx != bit_offset){
                std::cout<<"bit_idx: "<<bit_idx<<"; bit_offset:"<<bit_offset<<std::endl;
                assert(bit_idx == bit_offset);
            }

            if(curr_node->_end_node[curr_edge] == nullptr)
            {
                curr_node->_end_node[curr_edge] = new Node();
            }
            curr_node = curr_node->_end_node[curr_edge];

            bit_val = getBit(copied_word, bit_idx);

            // If there is no matching Edge
            if(curr_node->_word_ptr[bit_val] == nullptr)
            {
                // add the rest of copied_word as new Edge and it's value in the node at the end
                curr_node->addLongEdge(bit_val, copied_word, bit_idx, bit_count - bit_idx, data);
                return;
            }
            else // Get the matching Edge
            {
                curr_edge = bit_val;
            }
        }
    }

    // If end of copied_word is reached but not end of edge we insert an internal (non branch) node
    if(bit_idx != bit_offset + curr_node->_length[curr_edge])
    {
        unsigned char old_length = curr_node->_length[curr_edge];
        Node * old_node = curr_node->_end_node[curr_edge];
        unsigned char end_length = old_length - (bit_idx - bit_offset);
        unsigned char middle_length = old_length - end_length;
        curr_node->_length[curr_edge] = middle_length;
        // new Node for the value of copied_word
        curr_node->_end_node[curr_edge] = new Node(data);
        // the next bit of the longer sequence that was on the edge before
        bit_val = getBit(curr_node->_word_ptr[curr_edge], bit_idx);
        // new Edge with the rest of the longer sequence that was on the edge before
        curr_node->_end_node[curr_edge]->addShortEdge(
             bit_val, curr_node->_word_ptr[curr_edge], end_length);
        // and it's original node at the end
        curr_node = curr_node->_end_node[curr_edge];
        curr_node->_end_node[bit_val] = old_node;
    }
    else // else we change (overwrite) the value that was currently in the tree or add new one
    {
        // should not be nullptr
        assert(curr_node->_end_node[curr_edge] != nullptr);
        curr_node->_end_node[curr_edge]->_value = data;
    }

    // in both of the last cases we don't need copied_word
    dictionary.pop_back();
    delete [] copied_word;
}

// gets the data for a word if it is contained in the tree
// else returns a number < 0
int RadixPatricia::find(const char* word)
{
    Node * node = findNode(word, true);
    if(node != nullptr)
    {
        return node->_value;
    }
    else
    {
        return -1;
    }
}

inline Node * RadixPatricia::findNode(const char* prefix, bool exact_match)
{
	// Bad input
    if(prefix == nullptr || prefix == NULL)
    {
        return nullptr;
    }

    long long symbol_count = (long long) strlen(prefix);

    // Empty string is recognised!
    if(symbol_count <= 0)
    {
        return _root;
    }

    // total number of bits word is long
    long long bit_count = symbol_count * CHAR_SIZE_BITS;
    // current bit index with respect to word
    long long bit_idx = 0;
    // offset (with respect to word) at wich curr_edge starts
    long long bit_offset = 0;

    Node * curr_node = _root;
    // the value of the first bit of word
    bool bit_val = getBit(prefix, bit_idx);

    // get the starting edge
    bool curr_edge = bit_val;

    // if there is no edge to start from (the first 0/1 doesn't match) return -1
    if(curr_node->_word_ptr[curr_edge] == nullptr)
    {
        return nullptr;
    }

    ++bit_idx;

    while (bit_idx < bit_count)
    {
        // The index relative to the current edge's beginning index (is computed, not explicitly stored)
        int edge_relative_idx = bit_idx - bit_offset;

        // We are still inside the current_edge
        if(edge_relative_idx < curr_node->_length[curr_edge])
        {
            // There was a check for more bits at a time here, but
            // char by char was slower than bit by bit
            // and more than 8 bits at a time was architecture specific and buggy

            bit_val = getBit(prefix, bit_idx);
            bool edge_bit_val = getBit(curr_node->_word_ptr[curr_edge], bit_idx);

            // If different bit is found - return -1
            if(bit_val != edge_bit_val)
            {
                return nullptr;
            }

            ++bit_idx;
        }
        else // End of curr_edge was reached
        {
            bit_offset += curr_node->_length[curr_edge];
            assert(bit_idx == bit_offset);

            curr_node = curr_node->_end_node[curr_edge];

            if(curr_node == nullptr)
            {
                return nullptr;
            }

            bit_val = getBit(prefix, bit_idx);

            curr_edge = bit_val;
            // If there is no matching Edge return -1
            if(curr_node->_word_ptr[curr_edge] == nullptr)
            {
                return nullptr;
            }
        }
    }

    // If end of word is reached but not end of edge simultaneously - return -1
    if(bit_idx != bit_offset + curr_node->_length[curr_edge])
    {
        if(exact_match)
        {
            return nullptr;
        }
        else
        {
            // this is used to get all words with prefix, not exact match
            return curr_node->_end_node[curr_edge];
        }
    }
    else // else we return the found value
    {
        curr_node = curr_node->_end_node[curr_edge];
        assert(curr_node != nullptr);
        return curr_node;
    }
}

// returns a vector with all values of words in the tree starting with prefix
std::vector<int> RadixPatricia::getAllWithPrefix(const char* prefix)
{
    std::vector<int> values;
    Node * startingNode = findNode(prefix, false);
    recursiveAddValue(startingNode, values);
	return values;
}

void RadixPatricia::recursiveAddValue(Node * node, std::vector<int>& vec)
{
    if(node == nullptr)
    {
        return;
    }

    if (node->_value >= 0)
    {
        vec.push_back(node->_value);
    }

    if(node->_end_node[0] != nullptr)
    {
        recursiveAddValue(node->_end_node[0], vec);
    }

    if(node->_end_node[1] != nullptr)
    {
        recursiveAddValue(node->_end_node[1], vec);
    }
}

// removes a word from the tree, returns true on success
// (there was a word like that and it was removed) and false otherwise
bool RadixPatricia::remove(const char* word)
{
    // just a trivial implementation, doesn't delete nodes, memory of words, etc.
    Node * node = findNode(word, true);
    if(node != nullptr && node -> _value != -1)
    {
        node -> _value = -1;
        return true;
    }

	return false;
}
