#!/bin/bash

# Script to setup the database and environment variables.
# Author: Sergiu Chirap

# Sets environment variables and executes necessary SQL files for setting up schemas and roles.

set -e  # Exit immediately if a command exits with a non-zero status.

echo "Starting database setup and environment configuration."

# Set environment variables.
echo "Setting environment variables..."
export UnDB_NAME='postgres'
export UnDB_USER='backend'
export UnDB_PASSWORD='Y±ZDÎþ(d?°Ö9Ö¨¨-Ä)üw]«:g`ë!*Â×®Ý_9¢)aNWq'
echo "Environment variables set."

# Execute SQL file.
echo "Running SQL setup script..."
psql -U postgres -d postgres -v ON_ERROR_STOP=1 -f setup.sql --set unServer="$UnDB_USER" --set unPass="$UnDB_PASSWORD"
echo "SQL setup script executed."

echo "Database setup and environment configuration complete."