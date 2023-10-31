#!/bin/bash

if [ $# -ne 1 ]; then
  # Check that a valid error message has been provided
  echo "Error: missing error message"
  exit 1
fi

# Set the path to the CSV file
file_path="error_codes.csv"

# Get the error message from the command line argument
error_message=$1

# Create a random number generator
random=$RANDOM

# Loop until a unique random number is found
while true; do
  # Generate a random number between 0 and 999
  random_number=$((random % 1000))

  # Left pad the random number with zeros to a length of 3
  formatted_number=$(printf "%03d" $random_number)

  # Check if the random number is already present in the CSV file
  if ! grep -q "^$formatted_number," "$file_path"; then
    # If the number is not found, print it and append it to the CSV file
    echo $formatted_number
    echo "$formatted_number,$error_message" >> "$file_path"
    break
  fi
done

sort -t ',' -k 1 "$file_path" -o "$file_path"
