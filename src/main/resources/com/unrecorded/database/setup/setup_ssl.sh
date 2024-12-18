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


# Script to configure SSL for PostgreSQL.

# Configures SSL for PostgreSQL using OpenSSL-generated certificates. 
# This ensures that your PostgreSQL server can handle encrypted connections.

set -e  # Exit immediately if a command exits with a non-zero status.

echo "Starting SSL configuration for PostgreSQL."

# Directory definitions.
CERT_DIR="/etc/postgresql/ssl"
PG_CONF="/var/lib/pgsql/16/data/postgresql.conf"
PG_HBA="/var/lib/pgsql/16/data/pg_hba.conf"

# Create directory for certificates if it doesn't exist.
echo "Creating SSL certificate directory: $CERT_DIR"
sudo mkdir -p $CERT_DIR
sudo chmod 700 $CERT_DIR
echo "SSL certificate directory created."

# Generate server key.
echo "Generating server key..."
sudo openssl genrsa -out $CERT_DIR/server.key 2048
sudo chmod 600 $CERT_DIR/server.key
echo "Server key generated and secured."

# Generate server certificate signing request.
echo "Generating server certificate signing request..."
sudo openssl req -new -key $CERT_DIR/server.key -out $CERT_DIR/server.csr -subj "/CN=$(hostname)"
echo "Server certificate signing request generated."

# Generate self-signed server certificate.
echo "Generating self-signed server certificate..."
sudo openssl x509 -req -in $CERT_DIR/server.csr -signkey $CERT_DIR/server.key -out $CERT_DIR/server.crt -days 365
sudo chmod 600 $CERT_DIR/server.crt
echo "Self-signed server certificate generated."

# Update PostgreSQL configuration files.
echo "Updating PostgreSQL configuration for SSL..."
sudo bash -c "echo 'ssl = on' >> $PG_CONF"
sudo bash -c "echo 'ssl_cert_file = ''$CERT_DIR/server.crt'' ' >> $PG_CONF"
sudo bash -c "echo 'ssl_key_file = ''$CERT_DIR/server.key'' ' >> $PG_CONF"
echo "PostgreSQL configuration updated for SSL."

# Update 'pg_hba.conf'.
echo "Updating pg_hba.conf for SSL connections..."
sudo bash -c "echo 'hostssl all all 0.0.0.0/0 scram-sha-256' >> $PG_HBA"
echo "pg_hba.conf updated for SSL connections."

# Restart PostgreSQL to apply changes.
echo "Restarting PostgreSQL service..."
sudo service postgresql restart
echo "PostgreSQL service restarted."

echo "PostgreSQL SSL setup complete."