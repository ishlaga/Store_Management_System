#!/bin/bash

# Create output directory if it doesn't exist
mkdir -p out

# Compile using a single find command to locate all Java files
find src/main/java -name "*.java" -print0 | xargs -0 javac -d out

# Run the program if compilation was successful
if [ $? -eq 0 ]; then
    java -cp out Main
else
    echo "Compilation failed"
fi