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


# Script to install PostgreSQL 16 on Amazon Linux or Amazon Linux 2.

# Updates the system, installs necessary dependencies, and sets up PostgreSQL 16.
# This script sets the stage by ensuring that PostgreSQL is properly installed and running.

set -e  # Exit immediately if a command exits with a non-zero status.

echo "Starting PostgreSQL 16 installation on Amazon Linux."

# Update the system.
echo "Updating system packages..."
sudo yum update -y
echo "System update complete."

# Install wget if not already installed.
echo "Checking and installing wget if needed..."
sudo yum install -y wget
echo "Wget check and installation complete."

# Add the PostgreSQL 16 repository.
echo "Adding PostgreSQL 16 repository..."
sudo yum install -y https://download.postgresql.org/pub/repos/yum/16/redhat/rhel-7-x86_64/pgdg-redhat-repo-latest.noarch.rpm
echo "PostgreSQL 16 repository added."

# Disable existing PostgreSQL module (if necessary).
echo "Disabling any existing PostgreSQL modules if applicable..."
sudo amazon-linux-extras disable postgresql13
echo "Disabled existing PostgreSQL module."

# Install PostgreSQL 16.
echo "Installing PostgreSQL 16..."
sudo yum install -y postgresql16-server postgresql16
echo "PostgreSQL 16 installation complete."

# Initialize the PostgreSQL database.
echo "Initializing PostgreSQL database..."
sudo /usr/pgsql-16/bin/postgresql-16-setup initdb
echo "PostgreSQL database initialized."

# Enable PostgreSQL to start on boot.
echo "Configuring PostgreSQL to start on boot..."
sudo systemctl enable postgresql-16
echo "Configured PostgreSQL to start on boot."

# Start the PostgreSQL service.
echo "Starting PostgreSQL service..."
sudo systemctl start postgresql-16
echo "PostgreSQL service started."

# Display the PostgreSQL version to confirm installation.
echo "Checking PostgreSQL version to confirm installation..."
psql --version
echo "PostgreSQL installation and configuration complete."