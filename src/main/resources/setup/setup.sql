-- Script to set up the necessary database schema.
-- Author: Sergiu Chirap

-- Drop & create the 'unrecorded' schema to organize database objects related to this application.
DROP SCHEMA IF EXISTS unrecorded CASCADE;
CREATE SCHEMA IF NOT EXISTS unrecorded;
SET search_path TO unrecorded;

-- Drop & create a new role with the appropriate password for database interaction.
DROP ROLE IF EXISTS :unServer;
CREATE ROLE :unServer WITH ENCRYPTED PASSWORD :unPass;

-- Grant necessary permissions to the role for connecting and using schema objects.
GRANT CONNECT ON DATABASE postgres TO :unServer;
GRANT USAGE ON SCHEMA unrecorded TO :unServer;
GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA unrecorded TO :unServer;

-- Ensure the required extensions are available for UUID generation, plpgsql functions, and job scheduling.
CREATE EXTENSION IF NOT EXISTS plpgsql;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS pg_cron;

-- Create 'Users' table to manage user account information, enforce unique constraints for core identifiers.
CREATE TABLE IF NOT EXISTS postgres.unrecorded.users (
    user_id               UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,        -- User ID as a primary key with default UUID.
    username              VARCHAR(30)                                 NOT NULL UNIQUE, -- Enforcing unique usernames.
    password_hash         VARCHAR                                     NOT NULL,
    password_salt         BYTEA                                       NOT NULL,
    email                 VARCHAR(254)                                NOT NULL UNIQUE, -- Unique email for communication.
    public_key            TEXT                                        NOT NULL UNIQUE, -- Public key for encryption.
    private_key_encrypted TEXT                                        NOT NULL
);

-- Create 'Friendships' table to track user relationships and their status.
CREATE TABLE IF NOT EXISTS postgres.unrecorded.friendships (
    user_id_1 UUID REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE CASCADE, -- Handles user deletions seamlessly.
    user_id_2 UUID REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE CASCADE,
    status    CHAR(3) CHECK (status IN ('FRD', 'UNK', 'PND')) NOT NULL,            -- Possible statuses: FRD (Friend), UNK (Unknown), PND (Pending).
    PRIMARY KEY (user_id_1, user_id_2)
);

-- Create 'Groups' table to manage group information and administrator assignments.
CREATE TABLE IF NOT EXISTS postgres.unrecorded.groups (
    group_id   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),                                         -- Unique group identifier.
    group_name VARCHAR(50) NOT NULL,
    admin_id   UUID        NOT NULL REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE SET NULL -- Protect admin deletions.
);

-- Create 'Group Members' table to manage group memberships and roles.
CREATE TABLE IF NOT EXISTS postgres.unrecorded.group_members (
    group_id UUID REFERENCES groups (group_id) ON UPDATE CASCADE ON DELETE CASCADE, -- Auto-delete memberships on group removal.
    user_id  UUID REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE CASCADE,   -- Auto-delete memberships on user removal.
    role     VARCHAR(50) NOT NULL,                                                  -- Define a role within the group.
    PRIMARY KEY (group_id, user_id)
);

-- Create 'Messages' table to log communications between users with flexible recipient handling.
CREATE TABLE IF NOT EXISTS postgres.unrecorded.messages (
    message_id        UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sender_id         UUID                           NOT NULL REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE SET NULL, -- Detach messages on user deletion.
    deleted_sender    UUID,                                                                                                    -- Placeholder for soft delete logic.
    recipient_id      UUID                           REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE SET NULL,          -- Allows recipient flexibility on deletion.
    deleted_recipient UUID,                                                                                                    -- Placeholder for soft delete logic.
    group_id          UUID REFERENCES groups (group_id) ON UPDATE CASCADE ON DELETE CASCADE,
    is_group          BOOLEAN          DEFAULT false NOT NULL,
    content_encrypted TEXT,
    timestamp         TIMESTAMPTZ                    NOT NULL,                                                                 -- Captures message time.
    is_deleted        BOOLEAN                        NOT NULL,                                                                 -- Flag for deletion state.
    CHECK ((recipient_id IS NOT NULL AND group_id IS NULL) OR
           (recipient_id IS NULL AND group_id IS NOT NULL))                                                                    -- Enforce either individual or group messaging.
);

-- Create 'Reactions' table to record user interactions with messages.
CREATE TABLE IF NOT EXISTS postgres.unrecorded.reactions (
    message_id UUID REFERENCES messages (message_id) ON UPDATE CASCADE ON DELETE CASCADE, -- Cascade deletes with messages.
    user_id    UUID REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE CASCADE,       -- Cascade deletes with users.
    emoji      VARCHAR(4),                                                                -- Store reaction emoji.
    PRIMARY KEY (message_id, user_id, emoji)
);

-- Create 'Sessions' table to manage user login sessions with expiration.
CREATE TABLE IF NOT EXISTS postgres.unrecorded.sessions (
    session_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id    UUID        NOT NULL REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE CASCADE, -- Cascade session removal with user.
    token      TEXT        NOT NULL,                                                                -- Store session token.
    expires_at TIMESTAMPTZ NOT NULL                                                                 -- Define session expiration.
);

-- Create 'Notifications' table to manage system notifications for users.
CREATE TABLE IF NOT EXISTS postgres.unrecorded.notifications (
    notification_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID        NOT NULL REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE CASCADE, -- Responsible cleanup with user deletion.
    type            VARCHAR(15) NOT NULL,                                                                -- Notification type info.
    content         TEXT        NOT NULL,                                                                -- Content details.
    is_read         BOOLEAN     NOT NULL,                                                                -- Read a state flag.
    timestamp       TIMESTAMPTZ NOT NULL                                                                 -- Noting when notification was generated.
);

-- Create 'DAE' table for detailed logging of database application errors.
CREATE TABLE IF NOT EXISTS postgres.unrecorded.dae (
    dae_id          UUID PRIMARY KEY, error_message TEXT NOT NULL, -- Storing error messages.
    error_timestamp TIMESTAMPTZ                          NOT NULL, -- When error occurred.
    operation       VARCHAR(10)                          NOT NULL, -- Operation context.
    related_entity  VARCHAR(25)                          NOT NULL, -- Related database entity.
    stack_trace     TEXT                                 NOT NULL, -- Stack trace info.
    additional_info TEXT                                           -- Placeholder for extra context.
);

-- Create indexes to optimize search queries.
CREATE INDEX username_index ON postgres.unrecorded.users (username); -- For faster username lookups.
CREATE INDEX email_index ON postgres.unrecorded.users (email); -- For faster email searches.
CREATE INDEX public_key_index ON postgres.unrecorded.users (public_key); -- For public key searching.
CREATE INDEX status_index ON postgres.unrecorded.friendships (status); -- For efficient status queries.
CREATE INDEX timestamp_index ON postgres.unrecorded.messages (timestamp); -- For retrieving messages by time.
CREATE INDEX deleted_sender_index ON postgres.unrecorded.messages (deleted_sender); -- For sender soft delete tracking.
CREATE INDEX deleted_recipient_index ON postgres.unrecorded.messages (deleted_recipient); -- For recipient soft delete tracking.

-- Function to handle user-related cleanup before user deletion.
CREATE OR REPLACE FUNCTION postgres.unrecorded.before_user_deletion() RETURNS TRIGGER AS
$$
BEGIN
    -- Soft delete by setting deleted_sender and deleted_recipient fields.
    UPDATE postgres.unrecorded.messages
    SET deleted_sender = OLD.user_id -- Mark sender as deleted.
    WHERE sender_id = OLD.user_id;

    UPDATE postgres.unrecorded.messages
    SET deleted_recipient = OLD.user_id -- Mark recipient as deleted.
    WHERE recipient_id = OLD.user_id;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;


-- Function to manage redistribution of group admin responsibilities or delete group after user deletion.
CREATE OR REPLACE FUNCTION postgres.unrecorded.after_user_deletion() RETURNS TRIGGER AS
$$
DECLARE
    new_admin        UUID; -- Variable for storing new admin ID.
    current_group_id UUID; -- Variable for storing current group ID.
BEGIN
    -- Iterate over any groups where the admin has been removed.
    FOR current_group_id IN
        SELECT group_id FROM postgres.unrecorded.groups WHERE admin_id IS NULL
        LOOP
            -- Attempt to assign a new admin from existing group members.
            SELECT user_id
            INTO new_admin
            FROM postgres.unrecorded.group_members
            WHERE group_id = current_group_id
            ORDER BY random() -- Randomly select a group member as a new admin.
            LIMIT 1;

            -- If no members remain, delete the group.
            IF new_admin IS NULL THEN
                DELETE FROM postgres.unrecorded.groups WHERE group_id = current_group_id;
            ELSE
                -- Otherwise, update the group's admin to the newly selected candidate.
                UPDATE postgres.unrecorded.groups
                SET admin_id = new_admin
                WHERE group_id = current_group_id;
            END IF;
        END LOOP;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Function to manage actions on session creation or update.
CREATE OR REPLACE FUNCTION manage_session() RETURNS TRIGGER AS
$$
BEGIN
    IF (NEW.expires_at <= CURRENT_TIMESTAMP) THEN
        RAISE EXCEPTION 'Session is already expired!'; -- Ensure valid session timings.
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to call before_user_deletion function before deleting a user.
CREATE OR REPLACE TRIGGER before_user_deletion
    BEFORE DELETE
    ON postgres.unrecorded.users
    FOR EACH ROW
EXECUTE FUNCTION postgres.unrecorded.before_user_deletion(); -- Execute cleanup before user removal.

-- Trigger to call after_user_deletion function after deleting a user.
CREATE OR REPLACE TRIGGER after_user_deletion
    AFTER DELETE
    ON postgres.unrecorded.users
    FOR EACH ROW
EXECUTE FUNCTION postgres.unrecorded.after_user_deletion(); -- Handle admin reassignments.

-- Trigger to call manage_session function before creating/updating a session.
CREATE OR REPLACE TRIGGER before_session_insert_or_update
    BEFORE INSERT OR UPDATE
    ON postgres.unrecorded.sessions
    FOR EACH ROW
EXECUTE FUNCTION manage_session(); -- Validate sessions on creation/update.

-- Housekeeping: Delete notifications that are read or related to deleted entities.
CREATE OR REPLACE FUNCTION postgres.unrecorded.notification_housekeeping() RETURNS VOID AS
$$
BEGIN
    -- Delete notifications marked as read or linked to non-existent users.
    DELETE
    FROM postgres.unrecorded.notifications
    WHERE (is_read = TRUE) -- Remove read notifications.
       OR NOT EXISTS (SELECT 1
                      FROM postgres.unrecorded.users
                      WHERE users.user_id = notifications.user_id); -- Remove those without a valid user.
END;
$$ LANGUAGE plpgsql;

-- Schedule the notification cleanup task to run daily at 1 AM.
SELECT cron.schedule('notification_cleanup', '0 1 * * *',
                     'SELECT postgres.unrecorded.notification_housekeeping();'); -- Automate cleanup task.