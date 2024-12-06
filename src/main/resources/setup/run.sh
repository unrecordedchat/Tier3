#!/bin/bash

# Master script to run all setup scripts in order for PostgreSQL 16 configuration.
# Author: Sergiu Chirap

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