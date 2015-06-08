
#include "node.hpp"
#include <iostream>

/**
 * @author Spas Kyuchukov
 */

bool getBit(const char * word_ptr, long long bit)
{
    long long symbol_idx = bit / CHAR_SIZE_BITS;
    long long bit_idx = bit % CHAR_SIZE_BITS;
    const char * symbol_ptr = word_ptr + symbol_idx;
    bit_idx = CHAR_SIZE_BITS - bit_idx - 1;
    unsigned char symbol = *symbol_ptr;
    return symbol & (1 << bit_idx);
}

Node::Node(int value /*= -1*/)
{
    _value = value;
    _word_ptr[0] = nullptr;
    _word_ptr[1] = nullptr;
    _length[0] = 0;
    _length[1] = 0;
    _end_node[0] = nullptr;
    _end_node[1] = nullptr;
    ++node_count;
}

void Node::addLongEdge(bool idx, const char * word,
    long long begin_bit, long long length, int end_node_val /*= -1*/)
{
    // should break into more nodes
    if(length > UNSIGNED_CHAR_MAX_VAL)
    {
        ++edge_count;
        _word_ptr[idx] = word;
        _length[idx] = UNSIGNED_CHAR_MAX_VAL;
        _end_node[idx] = new Node();
        length -= UNSIGNED_CHAR_MAX_VAL;
        begin_bit += UNSIGNED_CHAR_MAX_VAL;
        _end_node[idx]->addLongEdge(getBit(word, begin_bit), word, begin_bit, length, end_node_val);
    }
    else
    {
        addShortEdge(idx, word, length, end_node_val);
    }
}

void Node::addShortEdge(bool idx, const char * word, long long length, int end_node_val /*= -1*/)
{
    ++edge_count;
    _word_ptr[idx] = word;
    _length[idx] = length;
    _end_node[idx] = (end_node_val != -1) ? (new Node(end_node_val)) : nullptr;
}

Node::~Node()
{// words are deleted after that in the tree's destructor
    --node_count;
    if(_end_node[0] != nullptr)
    {
        delete _end_node[0];
    }

    if(_end_node[1] != nullptr)
    {
        delete _end_node[1];
    }
}
