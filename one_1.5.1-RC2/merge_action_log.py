#!/usr/bin/env python

try:
    with open('./reports/actions.json', 'r') as input:
        with open('./reports/actions_merged.json', 'w') as merged:
            merged.write('[')
            elements = ', '.join(input)
            merged.write(elements)
            merged.write(']')
except:
    pass