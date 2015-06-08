
#include "RadixPatricia.hpp"

#include <iostream>
#include <fstream>
#include <string>

#include <string.h>
#include <assert.h>
#include <cstdlib>

#include <algorithm>
#include <functional>
#include <iterator>
#include <numeric>
#include <chrono>

/**
 * @author Spas Kyuchukov
 */

using namespace std;

long long Node::node_count = 0;
long long Node::edge_count = 0;

bool compareResults(const pair<string, long long>& p1, const pair<string, long long>& p2)
{
    return p1.second > p2.second;
}

int main(int argc, char** argv)
{
    RadixPatricia * tree = new RadixPatricia();

    //argc = 3;
    //argv = new char*[3]{"program.exe", "dict.txt", "text.txt"};
    // first is name of program, second is dictionary with values, third is text to calculate values for
    if (argc == 3)
    {
        string line;
        ifstream dict (argv[1]);
        int i = 0;
        if (dict.is_open())
        {
            while (getline(dict, line))
            {
                int separator_idx = line.find_last_of(' ');
                string word = line.substr(0, separator_idx);
                string val_str = line.substr(separator_idx + 1, line.length() - separator_idx - 1);
                // atoi used insted of c++11 stoi because of MinGW bug
                int val = atoi(val_str.c_str());
                tree->insert(word.c_str(), val);
                ++i;
            }
            dict.close();
        }
/*
        ifstream dict2 (argv[1]);
        i = 0;
        if (dict2.is_open())
        {
            while (getline(dict2, line))
            {
                int separator_idx = line.find_last_of(' ');
                string word = line.substr(0, separator_idx);
                string val_str = line.substr(separator_idx + 1, line.length() - separator_idx - 1);
                // atoi used insted of c++11 stoi because of MinGW bug
                int val_exp = atoi(val_str.c_str());

                int val = tree->find(word.c_str());
                assert(val == val_exp);
                //cout<<line<<": "<<val<<"; exp = "<<i<<endl;
                ++i;
            }
            dict2.close();
        }
*/
        vector<pair<string, long long> > results;

        ifstream txt (argv[2]);
        if (txt.is_open())
        {
            while (getline(txt, line, ' '))
            {
                vector<int> values = tree->getAllWithPrefix(line.c_str());
                // let's hope this type is big enough
                long long sum = accumulate(begin(values), end(values), 0);
                results.push_back(pair<string, long long>(line, sum));
            }
            txt.close();
        }

        sort(begin(results), end(results), compareResults);

        for(pair<string, long long>& rec : results)
        {
            cout<<rec.first<<' '<<rec.second<<endl;
        }
    }
    else
    {
        cout<<"Too few arguments. Supply dictionary and text files!"<<endl;
    }

    return 0;
}
