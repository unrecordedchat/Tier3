#!/bin/bash

# VIA University College - School of Technology and Business
# Software Engineering Program - 3rd Semester Project
#
# This work is a part of the academic curriculum for the Software Engineering program at VIA University College.
# It is intended only for educational and academic purposes.
#
# No part of this project may be reproduced or transmitted in any form or by any means,
# except as permitted by VIA University and the course instructor.
# All rights reserved by the contributors and VIA University College.
#
# Project Name: Unrecorded
# Author: Sergiu Chirap
# Year: 2024


# Master script to run all setup scripts in order for PostgreSQL 16 configuration.

# Directory where all scripts are located.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Array of script names to execute in order.
SCRIPTS=(
    "install_postgresql16.sh"
    "install_openssl_cron.sh"
    "setup_ssl.sh"
    "setup_db.sh"
)

# Function to run each script and check for errors.
run_script() {
    local script="$SCRIPT_DIR/$1"
    if [[ -f "$script" && -x "$script" ]]; then
        echo "Running $1..."
        if ! "$script"; then
            echo "Error: $1 failed to execute. Exiting."
            exit 1
        fi
        echo "$1 executed successfully."
    else
        echo "Error: $1 not found or is not executable."
        exit 1
    fi
}

# Execute each script in the specified order.
for script in "${SCRIPTS[@]}"; do
    run_script "$script"
done

echo "All scripts executed successfully."