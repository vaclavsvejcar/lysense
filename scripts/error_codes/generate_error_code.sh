#!/bin/bash

# zen :: license header manager
# Copyright (c) 2022-2023 Vaclav Svejcar
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice,
#    this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright
#    notice, this list of conditions and the following disclaimer in the
#    documentation and/or other materials provided with the distribution.
# 3. Neither the name of copyright holder nor the names of its
#    contributors may be used to endorse or promote products derived from
#    this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.

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
