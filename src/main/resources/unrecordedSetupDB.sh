#!/bin/bash

set -e  # Exit immediately if a command exits with a non-zero status.

# Define directory for certificates
CERT_DIR="/etc/postgresql/ssl"

# Create directory for certificates if it doesn't exist
echo "Creating SSL certificate directory: $CERT_DIR"
sudo mkdir -p $CERT_DIR
sudo chmod 700 $CERT_DIR

# Generate server key
echo "Generating server key..."
sudo openssl genrsa -out $CERT_DIR/server.key 2048
sudo chmod 600 $CERT_DIR/server.key

# Generate server certificate signing request
echo "Generating server certificate signing request..."
sudo openssl req -new -key $CERT_DIR/server.key -out $CERT_DIR/server.csr -subj "/CN=$(hostname)"

# Generate self-signed server certificate
echo "Generating self-signed server certificate..."
sudo openssl x509 -req -in $CERT_DIR/server.csr -signkey $CERT_DIR/server.key -out $CERT_DIR/server.crt -days 365
sudo chmod 600 $CERT_DIR/server.crt

# Update PostgreSQL configuration files
echo "Updating PostgreSQL configuration for SSL..."
# Update postgresql.conf
PG_CONF="/etc/postgresql/12/main/postgresql.conf"
sudo bash -c "echo 'ssl = on' >> $PG_CONF"
sudo bash -c "echo 'ssl_cert_file = ''$CERT_DIR/server.crt'' ' >> $PG_CONF"
sudo bash -c "echo 'ssl_key_file = ''$CERT_DIR/server.key'' ' >> $PG_CONF"

# Update pg_hba.conf
PG_HBA="/etc/postgresql/12/main/pg_hba.conf"
sudo bash -c "echo 'hostssl all all 0.0.0.0/0 scram-sha-256' >> $PG_HBA"

# Restart PostgreSQL to apply changes
echo "Restarting PostgreSQL..."
sudo service postgresql restart

# Set environment variables
echo "Setting environment variables..."
export UnDB_NAME='postgres'
export UnDB_USER='backend'
export UnDB_PASSWORD='Y±ZDÎþ(d?°Ö9Ö¨¨-Ä)üw]«:g`ë!*Â×®Ý_9¢)aNWq'

# Run the SQL file with passed environment variables
psql -U postgres -d postgres -v ON_ERROR_STOP=1 -f setup.sql --set unServer="$UnDB_USER" --set unPass="$UnDB_PASSWORD"

echo "PostgreSQL SSL setup and environment variable configuration complete."