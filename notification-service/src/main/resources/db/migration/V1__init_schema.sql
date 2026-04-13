CREATE TABLE notification_logs (
    id              BIGSERIAL       PRIMARY KEY,
    event_type      VARCHAR(100)    NOT NULL,
    channel         VARCHAR(20)     NOT NULL,
    recipient       VARCHAR(255)    NOT NULL,
    status          VARCHAR(20)     NOT NULL,
    payload         TEXT,
    error_message   TEXT,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notification_recipient ON notification_logs(recipient);
CREATE INDEX idx_notification_status ON notification_logs(status);
CREATE INDEX idx_notification_type ON notification_logs(event_type);
