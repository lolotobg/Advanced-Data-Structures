#ifndef NODE_HPP_INCLUDED
#define NODE_HPP_INCLUDED

/**
 * @author Spas Kyuchukov
 */

// Assume 1 byte has 8 bits
const int CHAR_SIZE_BITS = (sizeof(char) * 8);
const unsigned int UNSIGNED_CHAR_MAX_VAL = (1 << (sizeof(unsigned char) * 8)) - 1;

bool getBit(const char * word_ptr, long long bit);

class Node
{
public:
    static long long node_count;
    static long long edge_count;

    int _value;
    // 2 edges
    // we could use shared_pointer here so that deallocation is automatic
    // and no need for vector<char *> inside tree
	const char * _word_ptr[2];
    unsigned short _length[2];
    Node * _end_node[2];

	Node(int value = -1);

	void addLongEdge(bool idx, const char * word,
        long long begin_bit, long long length, int end_node_val = -1);

	void addShortEdge(bool idx, const char * word, long long length, int end_node_val = -1);

	~Node();
};
#endif // NODE_HPP_INCLUDED
