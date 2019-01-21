#!/bin/bash

CONTAINER_NAME=vault.server
DATA_DIR=vault/data

echo "[*] Init vault..."
docker exec $CONTAINER_NAME vault operator init 2>&1 | tee $DATA_DIR/keys.txt
export VAULT_TOKEN=$(grep 'Initial Root Token:' $DATA_DIR/keys.txt | awk '{print substr($NF, 1, length($NF)-1)}')
echo ${VAULT_TOKEN} 2>&1 | tee $DATA_DIR/root-token.txt

echo "[*] Unseal vault..."
docker exec $CONTAINER_NAME vault unseal $(grep 'Key 1:' $DATA_DIR/keys.txt | awk '{print $NF}')
docker exec $CONTAINER_NAME vault unseal $(grep 'Key 2:' $DATA_DIR/keys.txt | awk '{print $NF}')
docker exec $CONTAINER_NAME vault unseal $(grep 'Key 3:' $DATA_DIR/keys.txt | awk '{print $NF}')
