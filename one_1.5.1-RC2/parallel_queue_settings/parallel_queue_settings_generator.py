#!/usr/bin/python

from os.path import exists
from os import makedirs

# Q_RANDOM_MODE                 = 1;

# Q_ORDER_BY_ARRIVAL_TIME       = 2;
# Q_ORDER_BY_REPLICATIONS       = 3;
# Q_ORDER_BY_HOP_COUNT          = 4;
# Q_ORDER_BY_TTL                = 5;
# Q_ORDER_BY_PACKET_SIZE        = 6;

# Q_ORDER_BY_ARRIVAL_TIME_DESC  = 7;
# Q_ORDER_BY_REPLICATIONS_DESC  = 8;
# Q_ORDER_BY_HOP_COUNT_DESC     = 9;
# Q_ORDER_BY_TTL_DESC           = 10;
# Q_ORDER_BY_PACKET_SIZE_DESC   = 11;

# Scheme
# Scenario.name = [qm_11; qm_12; qm_13; qm_14; qm_15; qm_16; qm_21; qm_22; qm_23; ... ]
# Group.sendQueue =  [1; 1; 1; 1; 1; 1;  2; 2; 2; ... ]
# Group.dropPolicy = [1; 2; 3; 4; 5; 6;  1; 2; 3; ... ]


# All possible options for us
# sendQueueOptions = [1, 2, 3, 4, 10, 6]
# dropPolicyOptions = [1, 2, 8, 9, 5, 11]

# Simply everything!
sendQueueOptions  = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]
dropPolicyOptions = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]

# Total
total = len(sendQueueOptions) * len(dropPolicyOptions)

# Parallel amount
par = 3

# Make sure we are even
usualWorkload = int(total / par)
lastWorkload = total - (usualWorkload * (par - 1))
workLoads = [usualWorkload] * (par - 1) + [lastWorkload]

# Print to be sure
print('Workloads: ' + str(workLoads))

# Create scenario names
scenarioNames = []
sendQueues = []
dropPolicies = []
for x in sendQueueOptions:
    for y in dropPolicyOptions:
        sendQueues.append(x)
        dropPolicies.append(y)
        scenarioName = 'qm@' + str(x) + '_' + str(y)
        scenarioNames.append(scenarioName )


# Create batches
scenarioNameBatches = []
sendQueueBatches = []
dropPoliciyBatches = []
for workLoad in workLoads:
    scenarioNameBatch, scenarioNames = scenarioNames[:workLoad], scenarioNames[workLoad:]
    scenarioNameBatch = str(scenarioNameBatch).replace("'", "").replace(",", ";").replace("]", ";]")
    scenarioNameBatches.append(scenarioNameBatch)

    sendQueueBatch, sendQueues = sendQueues[:workLoad], sendQueues[workLoad:]
    sendQueueBatch = str(sendQueueBatch).replace(",", ";").replace("]", ";]")
    sendQueueBatches.append(sendQueueBatch)

    dropPolicyBatch, dropPolicies = dropPolicies[:workLoad], dropPolicies[workLoad:]
    dropPolicyBatch = str(dropPolicyBatch).replace(",", ";").replace("]", ";]")
    dropPoliciyBatches.append(dropPolicyBatch)


# Create the path for all scripts
path = 'generated/par' + str(par)

# Create directory
if not exists(path):
    makedirs(path)

# Create file names
files = [path + '/queue_mode_benchmark_settings_' + str(x) + '.txt' for x in range(1, par + 1)]

# Print each file
for idx, file in enumerate(files):
    fd = open(file, 'w')
    try:
        print('-' * 60)
        txt = ('Scenario.name = ' + scenarioNameBatches[idx] + '\n' +
               'Group.sendQueue = ' + sendQueueBatches[idx] + '\n' +
               'Group.dropPolicy = ' + dropPoliciyBatches[idx] + '\n')
        fd.write(txt)
        print('Generated script: ' + file)
        print('Content:')
        print(txt)
        print('-' * 60)
    finally:
        fd.close()

# Write the run script
runScript = 'generated/run' + str(par) + '.sh'
fd = open(runScript, 'w')
try:
    lines = ['#!/bin/sh', 'cd ..']
    for idx, file in enumerate(files):
        lines.append('./one.sh -b ' + str(workLoads[idx]) + ' parallel_queue_settings/generated/queue_mode_benchmark_settings_base.txt parallel_queue_settings/' + file + ' &')

    for _ in files:
        lines.append('fg')

    lines.append('cd parallel_queue_settings')

    line = '\n'.join(lines)
    print('-' * 60)
    print('Generated script: ' + runScript)
    print('Content:')
    print(line)
    fd.write(line)
    print('-' * 60)
finally:
    fd.close()