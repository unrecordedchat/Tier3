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


# Script to setup the database and environment variables.

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