#!/bin/sh

for filename in mappings/*.json; do
  BASENAME=$(basename "$filename" .json)
  echo "\nDeleting existing $BASENAME index..."
  curl -H 'Content-Type: application/json' -XDELETE "localhost:9200/$BASENAME?pretty"
  sleep 1
  echo "\nCreate mapping for $BASENAME index..."
  curl -H 'Content-Type: application/json' -XPUT "localhost:9200/$BASENAME" --data-binary "@$filename"
  sleep 1
done

echo "\n Done!"