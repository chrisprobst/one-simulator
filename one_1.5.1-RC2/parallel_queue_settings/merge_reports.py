#!/usr/bin/python

from os.path import exists, isfile, join
from os import makedirs, listdir
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
from matplotlib import colors as _colors

# The reports path
reports_path = '../reports/'

# All files
files = [f for f in listdir(reports_path)
           if isfile(join(reports_path, f))]

# All known modes
modes = {
    1: 'Random',

    2: 'Arrival\nTime\n(asc)',
    3: 'Replications\n(asc)',
    4: 'Relayed\nNodes\n(asc)',
    5: 'TTL\n(asc)',
    6: 'Packet\nSize\n(asc)',

    7: 'Arrival\nTime\n(desc)',
    8: 'Replications\n(desc)',
    9: 'Relayed\nNodes\n(desc)',
   10: 'TTL\n(desc)',
   11: 'Packet\nSize\n(desc)',
}

latex_modes = {
    1: 'Random',

    2: '$\\uparrow$Arrival\\\\Time',
    3: '$\\uparrow$Replications',
    4: '$\\uparrow$Relayed\\\\Nodes',
    5: '$\\uparrow$TTL',
    6: '$\\uparrow$Packet\\\\Size',

    7: '$\\downarrow$Arrival\\\\Time',
    8: '$\\downarrow$Replications',
    9: '$\\downarrow$Relayed\\\\Nodes',
    10: '$\\downarrow$TTL',
    11: '$\\downarrow$Packet\\\\Size',
}

csv_modes_flat = {
    1: 'Random',

    2: '$\\uparrow$ Arrival-Time',
    3: '$\\uparrow$ Replications',
    4: '$\\uparrow$ Relayed-Nodes',
    5: '$\\uparrow$ TTL',
    6: '$\\uparrow$ Packet-Size',

    7: '$\\downarrow$ Arrival-Time',
    8: '$\\downarrow$ Replications',
    9: '$\\downarrow$ Relayed-Nodes',
    10: '$\\downarrow$ TTL',
    11: '$\\downarrow$ Packet-Size',
}

# Get the queue mode content from the file path
def get_file_dict(file_path):
    d = {}
    # Extract file content
    fd = open(file_path, 'r')
    try:
        # Skip first line
        lines = fd.readlines()[1:]

        for line in map(str.strip, lines):
            k, v = line.split(':')
            d[k] = float(v)

        return d
    finally:
        fd.close()

def collect_files():
    # Keep track of all modes
    sendQueues, dropPolicies = set(), set()

    # All reports
    reports = {}

    # All possible fields
    fields = set()

    # Fill data structures
    for file in files:
        # Ignore wrong files
        if not file.startswith('qm@'):
            continue

        # Make file path
        file_path = join(reports_path, file)

        # Remove boring stuff and extract queue modes
        vars = file.replace('qm@', '').replace('_MessageStatsReport.txt', '')
        sendQueue, dropPolicy = vars.split('_')
        sendQueue = float(sendQueue)
        dropPolicy = float(dropPolicy)

        # Add modes
        sendQueues.add(sendQueue)
        dropPolicies.add(dropPolicy)

        # Get content
        content = get_file_dict(file_path)

        # Keep track of fields
        for k in content.keys():
            fields.add(k)

        # Store content in reports dictionary
        reports[(sendQueue, dropPolicy)] = content

    # The results dictionary
    results = {}

    # For all fields
    for field in fields:
        # The values
        values = {}

        # Iterate over each postion
        for sendQueue in sorted(sendQueues):
            for dropPolicy in sorted(dropPolicies):

                # Lookup report
                report = reports[(sendQueue, dropPolicy)]

                # Add value
                values[(sendQueue, dropPolicy)] = report[field]

        # Write plot file to disk
        results[field] = values

    # Return all results
    sendQueueRange, dropPolicyRange = list(sorted(sendQueues)), list(sorted(dropPolicies))
    sendQueuesNum, dropPoliciesNum = list(range(1, len(sendQueues) + 1)), list(range(1, len(dropPolicies) + 1))

    return modes, (sendQueueRange, sendQueuesNum), (dropPolicyRange, dropPoliciesNum), results


def make_csv_table(collect_files_result, field, rename=None):
    # Extract values
    modes, sendQueues, dropPolicies, results = collect_files_result
    sendQueueRange, sendQueuesNum = sendQueues
    dropPolicyRange, dropPoliciesNum = dropPolicies

    # Create names
    sendQueueNames, dropPolicyNames = [csv_modes_flat[i] for i in sendQueueRange], [latex_modes[i] for i in dropPolicyRange]

    # Fetch
    result = results[field]

    # Set title accordingly
    title = field if not rename else rename

    # Open file for writing
    fd = open(join('csv', title + '.csv'), 'w')
    prnt = lambda s: fd.write(s + '\n')

    try:
        for sendQueueMode in sendQueueRange:
            for dropQueueMode in dropPolicyRange:
                val = result[(sendQueueMode, dropQueueMode)]
                factor = 1
                if field == 'delivery_prob':
                    factor = 100
                if field == 'latency_avg':
                    factor = 1/60.0
                fd.write(str(val * factor).replace('.', ',') + ' ')
            fd.write('\n')


        s = '''
        fd.write('\\begin{table*}[htbp]\n')

        # Write header line
        headerLine = '|l|' + '|'.join(['c' for _ in dropPolicyRange]) + '|'
        headerLine = headerLine[:4] + '|' + headerLine[4:]
        headerLine = headerLine[:16] + '|' + headerLine[16:]
        fd.write('\\begin{tabular}{' + headerLine + '}\n')
        fd.write('\\hline\n')

        # Start with upper/left corner
        fd.write('\\diaghead{\\theadfont Buffer-management done}{Forwarding\\\\Strategy}{Dropping\\\\Policy}&')

        # Render the remaining columns
        columnLine = '&'.join(map(lambda n: '\\thead{' + n + '}', dropPolicyNames))
        fd.write(columnLine)

        fd.write('\\\\\n')
        fd.write('\\hline\n')

        # Render rows
        for idx, sendQueueName in enumerate(sendQueueNames):
            fd.write('\\footnotesize ' + sendQueueName)

            # Print all values
            vals = []
            sendQueueMode = sendQueueRange[idx]
            for dropQueueMode in dropPolicyRange:
                val = result[(sendQueueMode, dropQueueMode)]
                vals.append(str(val))
            fd.write(' & ' + ' & '.join(vals))

            fd.write('\\\\\n\\hline\n')

            if idx == 0 or idx == 5:
                fd.write('\\hline\n')

        fd.write('\\end{tabular}\n')
        fd.write('\\end{table*}\n')
        '''

    finally:
        fd.close()


def make_chart(collect_files_result, field, rename=None):
    # Extract values
    modes, sendQueues, dropPolicies, results = collect_files_result
    sendQueueRange, sendQueuesNum = sendQueues
    dropPolicyRange, dropPoliciesNum = dropPolicies

    # Fetch
    result = results[field]

    # Create plot graphs
    fig, ax = plt.subplots()

    # Width per bar
    width = 1.0 / len(dropPolicyRange) * 0.7

    # http://matplotlib.org/examples/color/named_colors.html
    # colors = ['firebrick', 'coral', 'orangered', 'peru', 'khaki', 'lightblue']
    colors = _colors.cnames.keys()

    # Render bars
    bars = []
    for iy, y in enumerate(dropPolicyRange):
        xs = map(lambda i: i + (width * iy) - (width * len(sendQueuesNum) / 2), sendQueuesNum)
        ys = [result[(x, y)] for x in sendQueueRange]
        bars.append(ax.bar(xs, ys, width, color=colors[iy]))

    # Set title, x and y label
    title = field if not rename else rename
    ax.set_title('Data: ' + title)
    ax.set_xlabel('Send queue ordered by')
    ax.set_ylabel('Value')
    fig.set_size_inches(18.5, 10.5)

    # Adjust x ticks
    start, end = ax.get_xlim()
    ax.xaxis.set_ticks([0] + sendQueueRange)
    ax.set_xticklabels([''] + map(lambda m: modes[m], sendQueueRange))

    # Render legend above
    box = ax.get_position()
    ax.set_position([box.x0, box.y0, box.width * 0.9, box.height])
    ax.legend(map(lambda x: x[0], bars),
              map(lambda m: modes[m], dropPolicyRange),
              title='Drop policy',
              loc='center right',
              bbox_to_anchor=(1.2, 0.5))

    # Render graph
    fig.savefig(join('charts', title + '.eps'))


def simulate_and_compute(fields):
    # Collect files
    collect_files_result = collect_files()

    # All fields we want to render
    for field in fields:
        a, b = field

        # We do not need charts anymore
        # make_chart(collect_files_result, a, b)

        # Create csv tables
        make_csv_table(collect_files_result, a, b)


# All fields we want to render
simulate_and_compute([
    ('delivery_prob', 'Delivery Ratio'),
    ('latency_avg', 'Average Delay'),
    ('overhead_ratio', 'Overhead Ratio'),
])