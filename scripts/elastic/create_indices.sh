#!/bin/sh
DIR="$(dirname $(readlink -f $0))"
ES_PORT=9200

while ! echo exit | nc localhost $ES_PORT; do sleep 10; done

for filename in $DIR/mappings/*.json; do
  BASENAME=$(basename "$filename" .json)
  echo "\nDeleting existing $BASENAME index..."
  curl -H 'Content-Type: application/json' -XDELETE "localhost:$ES_PORT/$BASENAME?pretty"
  sleep 1
  echo "\nCreate mapping for $BASENAME index..."
  curl -H 'Content-Type: application/json' -XPUT "localhost:$ES_PORT/$BASENAME" --data-binary "@$filename"
  sleep 1
done

echo "\n Done!"