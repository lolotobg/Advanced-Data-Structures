/*
    @author Spas Kyuchukov
    June 2015

    |Text| = n
    |Pattern| = m
    number ocuurances of Pattern in Text = k
        (occurances are allowed to overlap)

    Total 4*(n+1) memory (+n for copying string)

    Construction is the trivial - O(n^2 * log(n)) time, O(n) memory

    Search is close to optimum - O(m + logN + k) time
        k is in here because we want the starting indexes of all ocurrances

    The article in Wikipedia about LCP array explains shematically the
        asymptotic complexity of the serch and the algorithm
        (at least 1/4 of it :D, actually copied from a SO discussion).
*/

#include <iostream>
#include <cstring>
#include <vector>
#include <algorithm>

using namespace std;

// strcmp() but with extra int& parameter to hold the number of equal characters
// caller should guarantee that equal_char_count is within s1 and s2 bounds
inline int cmpStr(const char* s1, const char* s2, unsigned int& equal_char_count);

// (beg+end)/2
inline int getMiddle(int beg, int end);

// compare two suffixes with normal strcmp given their beginning indices
struct CmpSuffix
{
    const char * text;
    CmpSuffix(const char * txt) : text(txt){}

    bool operator()(int a, int b)
    {
        return strcmp((text + a), (text + b)) < 0;
    }
};

class SuffixArray
{
private:

    char * input_raw;
    unsigned length;

    unsigned * suff_arr;

    unsigned * LCP;
    unsigned * LCP_L;
    unsigned * LCP_R;

    // builds LCP_L and LCP_R arrays from LCP array
    void build_LCP_LR(int beg, int end);

    // can return -1!
    int lower_bound(const char * element);

public:
    SuffixArray(string text);
    ~SuffixArray();
    vector<int> search(const char* pattern);
};

SuffixArray::~SuffixArray()
{
    delete [] input_raw;

    delete [] suff_arr;

    delete [] LCP;
    delete [] LCP_L;
    delete [] LCP_R;
}

SuffixArray::SuffixArray(string text)
{
    length = text.length();
    input_raw =  new char[length + 1];
    strcpy(input_raw, text.c_str());

    suff_arr = new unsigned int[length + 1];
    unsigned int * rev_suff = new unsigned int[length + 1];

    suff_arr[0] = length;
    for (unsigned int i = 1; i <= length; i++)
    {
        suff_arr[i] = i - 1;
    }

    // O(n^2 * log(n)) time, O(n) memory
    // The sort is O(n * log(n)) comparisons each with O(n) because we compare strings with up to n chars
    CmpSuffix cmp(input_raw);
    sort(suff_arr + 1, suff_arr + length + 1, cmp);

    // Building reversed suffix array - mappings from suffix (beg idx) to it's rank
    // Used only while building LCP, then deleted
    for (unsigned int rank = 0; rank <= length; rank++)
    {
        int idx = suff_arr[rank];
        rev_suff[idx] = rank;
    }

    // valid range is [1...n] for us
    LCP = new unsigned int[length + 1];

    // O(n) time and memory builiding LCP
    /*
        LCP contains the longest common match i.e. number of equal characters
        between each pair of consecutive elements in the suffix array
    */
    int common = 0;
    for (unsigned int idx1 = 0; idx1 < length; idx1++)
    {
        int rank = rev_suff[idx1];
        int idx2 = suff_arr[rank - 1];
        while(input_raw[idx1 + common] == input_raw[idx2 + common])
        {
            ++common;
        }
        //LCP [1...n]
        LCP[rank] = common;

        if(common > 0)
        {
            --common;
        }
    }

    delete [] rev_suff;

    LCP[0] = 0;

    // O(n) time and memory to build LCP_L and LCP_R
    //[1...length-1] holds the values we are interested in
    /*
        LCP_L[i] is the number of equal character suff_arr[i] and suff_arr[X] have,
         where X is the index of the element that is left end of the binary search interval with middle i

        LCP_R[i] is the number of equal character suff_arr[i] and suff_arr[X] have,
         where X is the index of the element that is right end of the binary search interval with middle i
    */
    LCP_L = new unsigned int[length + 1];
    LCP_R = new unsigned int[length + 1];

    for (unsigned int i = 0; i <= length; i++)
    {
        LCP_L[i] = 0;
        LCP_R[i] = 0;
    }

    build_LCP_LR(0, length);
}

vector<int> SuffixArray::search(const char* pattern)
{
    vector<int> indexes;

    int pat_length = strlen(pattern);

    // O(m + logN)
    int lb = lower_bound(pattern);

    if(lb != -1)
    {
        unsigned int equal_chars = 0;
        // O(m) - check if we have at least one match
        cmpStr(input_raw + suff_arr[lb], pattern, equal_chars);

        // O(k)
        if(equal_chars == pat_length)
        {
            do
            {
                indexes.push_back(suff_arr[lb]);
                ++lb;
            }
            while (lb <= length && LCP[lb] >= pat_length);
        }
    }

    return indexes;
}

void SuffixArray::build_LCP_LR(int beg, int end)
{
    if(beg == end)
    {
        LCP_L[beg] = LCP[beg];
        LCP_R[beg] = LCP[beg];
        return;
    }

    int mid = getMiddle(beg, end);

    build_LCP_LR(beg, mid);
    build_LCP_LR(mid + 1, end);

    int mid_L = getMiddle(beg, mid);
    int mid_R = getMiddle(mid + 1, end);

    // all of these values are already calculated recursively
    LCP_L[mid] = min(LCP_L[mid_L], LCP_R[mid_L]);
    LCP_R[mid] = min(LCP_L[mid_R], LCP_R[mid_R]);
}

inline int cmpStr(const char* s1, const char* s2, unsigned int& equal_char_count)
{
    // not safe, programmer should guarantee that equal_char_count is within s1 and s2
    s1 += equal_char_count;
    s2 += equal_char_count;

    while(*s1 && (*s1==*s2))
    {
        ++s1;
        ++s2;
        ++equal_char_count;
    }

    return *((const unsigned char*)s1) - *((const unsigned char*)s2);
}

inline int getMiddle(int beg, int end)
{
    // (beg + end) / 2 but without the + overflow possibility
    return beg + (end - beg) / 2;
}

// can return -1 !
// binary search but comparison is with usage of complementary LCP, LCL_L, LCP_R arrays
int SuffixArray::lower_bound(const char * element)
{
    int left = -1, right = length + 1;
    int middle;

    bool got_left;
    bool left_by_string;

    unsigned int equal_chars = 0;

    if (right - left > 1)
    {
        middle = getMiddle(left, right);

        int cmp = cmpStr(input_raw + suff_arr[middle], element, equal_chars);

        if(cmp < 0)
        {
            left = middle;
            got_left = false;
            left_by_string = false;
        }
        else
        {
            right = middle;
            got_left = true;
            left_by_string = true;
        }
    }

    while (right - left > 1)
    {
        middle = getMiddle(left, right);

        unsigned int LPC_LR = (got_left) ? LCP_R[middle] : LCP_L[middle];

        if (equal_chars < LPC_LR)
        {
            if(left_by_string)
            {
                right = middle;
                got_left = true;
            }
            else
            {
                left = middle;
                got_left = false;
            }
        }
        else if (equal_chars > LPC_LR)
        {
            if(left_by_string)
            {
                left = middle;
                got_left = false;
            }
            else
            {
                right = middle;
                got_left = true;
            }

            equal_chars = LPC_LR;
        }
        else // if (equal_chars == LCP_LR[middle])
        {
            int cmp = cmpStr(input_raw + suff_arr[middle], element, equal_chars);

            if(cmp < 0)
            {
                left = middle;
                got_left = false;
                left_by_string = false;
            }
            else
            {
                right = middle;
                got_left = true;
                left_by_string = true;
            }
        }
    }

    return right;
}

int main()
{
    string text = "banana";

    cout<<"Text is '"<<text<<"'."<<endl;

    SuffixArray sa(text);

    string query;
    cout<<endl<<"Enter query or 'stop': ";
    cin>>query;
    cout<<endl;

    while (query != "stop")
    {
        vector<int> res = sa.search(query.c_str());

        cout<<"All suffixes starting with '"<<query<<"': "<<endl;

        for (int i = 0; i < res.size(); i++)
        {
            cout<<text.c_str() + res[i]<<endl;
        }

        cout<<endl<<"Enter query: ";
        cin>>query;
        cout<<endl;
    }

    return 0;
}
