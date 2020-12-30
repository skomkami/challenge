#!/bin/sh
DIR="$(dirname $(readlink -f $0))"

for filename in $DIR/example/*.json; do
  BASENAME=$(basename "$filename" .json)
  echo "\nAdding $BASENAME..."
  curl -H 'Content-Type: application/json' -XPOST "localhost:9200/$BASENAME/_bulk?pretty" --data-binary "@$filename"
  sleep 0.2
done

echo "\n Done!"