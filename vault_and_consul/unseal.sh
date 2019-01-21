#!/bin/bash

CONTAINER_NAME=vault.server
DATA_DIR=vault/data

echo "[*] Unseal vault..."
docker exec $CONTAINER_NAME vault unseal $(grep 'Key 1:' $DATA_DIR/keys.txt | awk '{print $NF}')
docker exec $CONTAINER_NAME vault unseal $(grep 'Key 2:' $DATA_DIR/keys.txt | awk '{print $NF}')
docker exec $CONTAINER_NAME vault unseal $(grep 'Key 3:' $DATA_DIR/keys.txt | awk '{print $NF}')
