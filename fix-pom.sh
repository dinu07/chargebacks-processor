#!/bin/bash
# Fix pom.xml name tag
sed -i '' 's/<n>/<name>/g; s/<\/n>/<\/name>/g' pom.xml

