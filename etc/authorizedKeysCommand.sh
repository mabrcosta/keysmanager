#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'
HOSTNAME=$(hostname)
URL="http://localhost:9000/machines/${HOSTNAME}/authorized_keys"
RESP=$(curl -s $URL)
echo "$RESP"
