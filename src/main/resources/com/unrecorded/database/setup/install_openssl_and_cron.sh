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


# Script to install OpenSSL & 'pg_cron' extension.

# Ensures OpenSSL is installed and configures PostgreSQL to support the `pg_cron` extension.
# This handles the additional configurations necessary for secure and scheduled operations within PostgreSQL.

set -e  # Exit immediately if a command exits with a non-zero status.

echo "Starting installation of OpenSSL and configuration of pg_cron extension."

# Update system packages.
echo "Updating system packages..."
sudo yum update -y
echo "System packages updated."

# Install OpenSSL if it's not already installed.
echo "Checking and installing OpenSSL..."
sudo yum install -y openssl
echo "OpenSSL installation complete."

# Verify OpenSSL installation.
echo "OpenSSL version:"
openssl version

# Install the EPEL repository (extra packages for enterprise Linux) for additional utilities.
echo "Installing EPEL repository..."
sudo yum install -y epel-release
echo "EPEL repository installed."

# Install PostgreSQL 16 client and devel package if not already installed (pg_cron requires it).
echo "Installing PostgreSQL 16 client and development package..."
sudo yum install -y postgresql16-devel postgresql16
echo "PostgreSQL 16 client and development package installed."

# Install pg_cron extension.
echo "Installing pg_cron extension..."
sudo yum install -y pg_cron_16
echo "pg_cron extension installed."

# Locate the shared_preload_libraries setting in the postgresql.conf file.
PG_CONF="/var/lib/pgsql/16/data/postgresql.conf"
echo "Using PostgreSQL configuration file at: $PG_CONF"

# Update the postgresql.conf to preload the pg_cron library.
echo "Updating shared_preload_libraries in postgresql.conf..."
if ! grep -q "shared_preload_libraries" "$PG_CONF"; then
    sudo bash -c "echo \"shared_preload_libraries = 'pg_cron'\" >> $PG_CONF"
    echo "shared_preload_libraries set to pg_cron."
else
    sudo sed -i "s/^#*\s*shared_preload_libraries\s*=\s*.*/shared_preload_libraries = 'pg_cron'/g" "$PG_CONF"
    echo "shared_preload_libraries updated to include pg_cron."
fi

# Restart PostgreSQL service to apply configuration changes.
echo "Restarting PostgreSQL service to apply configuration changes..."
sudo systemctl restart postgresql-16
echo "PostgreSQL service restarted."

# Verify the pg_cron extension is installed and active.
echo "Verifying pg_cron installation in PostgreSQL..."
sudo -u postgres psql -c "CREATE EXTENSION IF NOT EXISTS pg_cron;"
echo "pg_cron extension verified and active."

echo "Installation and configuration of OpenSSL and pg_cron complete."