DROP SCHEMA IF EXISTS Unrecorded CASCADE;
CREATE SCHEMA IF NOT EXISTS Unrecorded;

DROP ROLE IF EXISTS :unServer;
CREATE ROLE :unServer WITH ENCRYPTED PASSWORD :unPass;

GRANT CONNECT ON DATABASE postgres TO backend;
GRANT USAGE ON SCHEMA unrecorded TO backend;
GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA unrecorded TO backend;

CREATE EXTENSION IF NOT EXISTS plpgsql;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS postgres.unrecorded.Users (
    userId              UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    username            VARCHAR(30)                                 NOT NULL UNIQUE,
    passwordHash        VARCHAR                                     NOT NULL,
    passwordSalt        BYTEA                                       NOT NULL,
    emailAddress        VARCHAR(254)                                NOT NULL UNIQUE,
    publicKey           TEXT                                        NOT NULL UNIQUE,
    privateKeyEncrypted TEXT                                        NOT NULL
);

CREATE TABLE IF NOT EXISTS postgres.unrecorded.Friendships (
    userId1 UUID REFERENCES Users (userId) ON UPDATE CASCADE ON DELETE CASCADE,
    userId2 UUID REFERENCES Users (userId) ON UPDATE CASCADE ON DELETE CASCADE,
    status  CHAR(3) CHECK (status IN ('FRD', 'UNK', 'PND')) NOT NULL,
    PRIMARY KEY (userId1, userId2)
);

CREATE TABLE IF NOT EXISTS postgres.unrecorded.Groups (
    groupId   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    groupName VARCHAR(50) NOT NULL,
    adminId   UUID        NOT NULL REFERENCES Users (userId) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS postgres.unrecorded.GroupMembers (
    groupId UUID REFERENCES Groups (groupId) ON UPDATE CASCADE ON DELETE CASCADE,
    userId  UUID REFERENCES Users (userId) ON UPDATE CASCADE ON DELETE CASCADE,
    role    VARCHAR(50) NOT NULL,
    PRIMARY KEY (groupId, userId)
);

CREATE TABLE IF NOT EXISTS postgres.unrecorded.Messages (
    messageId        UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    senderId         UUID                           NOT NULL REFERENCES Users (userId) ON UPDATE CASCADE ON DELETE SET NULL,
    deletedSender    UUID,
    recipientId      UUID                           REFERENCES Users (userId) ON UPDATE CASCADE ON DELETE SET NULL,
    deletedRecipient UUID,
    groupId          UUID REFERENCES Groups (groupid) ON UPDATE CASCADE ON DELETE CASCADE,
    isGroup          BOOLEAN          DEFAULT false NOT NULL,
    contentEncrypted TEXT,
    timestamp        TIMESTAMPTZ                    NOT NULL,
    isDeleted        BOOLEAN                        NOT NULL,
    CHECK ((recipientId IS NOT NULL AND groupId IS NULL) OR (recipientId IS NULL AND groupId IS NOT NULL))
);

CREATE TABLE IF NOT EXISTS postgres.unrecorded.Reactions (
    messageId UUID REFERENCES Messages (messageId) ON UPDATE CASCADE ON DELETE CASCADE,
    userId    UUID REFERENCES Users (userId) ON UPDATE CASCADE ON DELETE CASCADE,
    emoji     VARCHAR(4),
    PRIMARY KEY (messageId, userId, emoji)
);

CREATE TABLE IF NOT EXISTS postgres.unrecorded.Sessions (
    sessionId UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    userId    UUID      NOT NULL REFERENCES Users (userId) ON UPDATE CASCADE ON DELETE CASCADE,
    token     TEXT      NOT NULL,
    expiresAt TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS postgres.unrecorded.Notifications (
    notificationId UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    userId         UUID         NOT NULL REFERENCES Users (userId) ON UPDATE CASCADE ON DELETE CASCADE,
    type           VARCHAR(255) NOT NULL,
    content        TEXT         NOT NULL,
    isRead         BOOLEAN      NOT NULL,
    timestamp      TIMESTAMPTZ  NOT NULL
);

CREATE TABLE IF NOT EXISTS postgres.unrecorded.DAE (
    daeId          UUID PRIMARY KEY,
    errorMessage   TEXT        NOT NULL,
    errorTimestamp TIMESTAMPTZ NOT NULL,
    operation      VARCHAR(10) NOT NULL,
    relatedEntity  VARCHAR(25) NOT NULL,
    stackTrace     TEXT        NOT NULL,
    additionalInfo TEXT
);

CREATE INDEX DeletedSenderIndex ON postgres.unrecorded.Messages (deletedSender);
CREATE INDEX DeletedRecipientIndex ON postgres.unrecorded.Messages (deletedRecipient);

CREATE OR REPLACE FUNCTION postgres.unrecorded.onUserDeletion() RETURNS TRIGGER AS
$$
DECLARE
    newAdmin UUID;
    currentGroupId UUID;
BEGIN
    -- Update messages to reflect deletion
    UPDATE postgres.unrecorded.Messages
    SET deletedSender = OLD.userId
    WHERE senderId = OLD.userId;

    UPDATE postgres.unrecorded.Messages
    SET deletedRecipient = OLD.userId
    WHERE recipientId = OLD.userId;

    -- Transfer admin to a random group member if the user is an admin
    FOR currentGroupId IN
        SELECT groupId FROM postgres.unrecorded.Groups WHERE adminId = OLD.userId
        LOOP
            -- Check for remaining members
            SELECT userId INTO newAdmin
            FROM postgres.unrecorded.GroupMembers
            WHERE groupId = currentGroupId AND userId != OLD.userId
            ORDER BY random()
            LIMIT 1;

            -- If no members are left, delete the group
            IF newAdmin IS NULL THEN
                DELETE FROM postgres.unrecorded.Groups WHERE groupId = currentGroupId;
            ELSE
                -- Update the Groups table to set the new admin
                UPDATE postgres.unrecorded.Groups
                SET adminId = newAdmin
                WHERE groupId = currentGroupId;
            END IF;
        END LOOP;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER OnUserDeletion
    BEFORE DELETE
    ON postgres.unrecorded.Users
    FOR EACH ROW
EXECUTE FUNCTION postgres.unrecorded.onUserDeletion();