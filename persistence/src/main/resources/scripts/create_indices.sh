#!/bin/sh

echo "\nDeleting existing users index..."
curl -H 'Content-Type: application/json' -XDELETE 'localhost:9200/users?pretty'
sleep 1
echo "\nCreate mapping for users index..."
curl -H 'Content-Type: application/json' -XPUT 'localhost:9200/users' --data-binary @mappings/users.json
sleep 1

echo "\nDeleting existing links index..."
curl -H 'Content-Type: application/json' -XDELETE 'localhost:9200/links?pretty'
sleep 1
echo "\nCreate mapping for links index..."
curl -H 'Content-Type: application/json' -XPUT 'localhost:9200/links' --data-binary @mappings/links.json
sleep 1

echo "\nDeleting existing votes index..."
curl -H 'Content-Type: application/json' -XDELETE 'localhost:9200/votes?pretty'
sleep 1
echo "\nCreate mapping for votes index..."
curl -H 'Content-Type: application/json' -XPUT 'localhost:9200/votes' --data-binary @mappings/votes.json
sleep 1

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