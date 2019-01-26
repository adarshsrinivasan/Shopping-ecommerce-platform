#!/bin/bash

CONTAINER_NAME=vault.server
DATA_DIR=vault/data
VAULT_TOKEN=$(grep 'Initial Root Token:' $DATA_DIR/keys.txt | awk '{print substr($NF, 1, length($NF)-1)}')

echo "[*] Unseal vault..."
docker exec $CONTAINER_NAME vault unseal $(grep 'Key 1:' $DATA_DIR/keys.txt | awk '{print $NF}')
docker exec $CONTAINER_NAME vault unseal $(grep 'Key 2:' $DATA_DIR/keys.txt | awk '{print $NF}')
docker exec $CONTAINER_NAME vault unseal $(grep 'Key 3:' $DATA_DIR/keys.txt | awk '{print $NF}')

echo "[*] Logging in vault..."
docker exec $CONTAINER_NAME vault login $VAULT_TOKEN

echo "[*] Writing secrets into vault..."
docker exec $CONTAINER_NAME vault write secret/catalog-service @/mnt/vault/data/catalog-service-credentials.json
docker exec $CONTAINER_NAME vault write secret/config-server @/mnt/vault/data/config-server-credentials.json
docker exec $CONTAINER_NAME vault write secret/inventory-service @/mnt/vault/data/inventory-service-credentials.json
