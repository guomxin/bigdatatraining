#!/usr/bin/env python

import sys

if __name__ == "__main__":
    last_word, total_count = None, 0
    for line in sys.stdin:
        word, count = line.strip().split('\t')
	count = int(count)
        if last_word and last_word != word:
            print "%s\t%d" % (last_word, total_count)
            last_word, total_count = (word, count)
        else:
            if not last_word:
                last_word = word
            total_count += count
    if last_word:
        print "%s\t%d" % (last_word, total_count)      
