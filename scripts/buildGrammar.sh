#!/bin/bash

rm -rf ../src/ru/spbau/tinydb/grammar/
mkdir ../src/ru/spbau/tinydb/grammar/

java -jar ../lib/antlr-4.4-complete.jar ../src/SQLGrammar.g4

mv ../src/SQLGrammar*.java ../src/ru/spbau/tinydb/grammar/
mv ../src/SQLGrammar*.tokens ../src/ru/spbau/tinydb/grammar/