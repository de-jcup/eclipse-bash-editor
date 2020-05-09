#!/bin/bash 

    function copyArtifactToAllServers()
    {
    ARTIFACT=$1
    for ((i=0; i < NUM_FRONTENDS; i++));
    do
    scp -q -o StrictHostKeyChecking=no $ARTIFACT $USER@${fe_servers[$i]}:$UPGRADES_FOLDER
    done

    for ((i=0; i < NUM_BACKENDS; i++));
    do
    scp -q -o StrictHostKeyChecking=no $ARTIFACT $USER@${be_servers[$i]}:$UPGRADES_FOLDER
    done
    }

echo "done"
