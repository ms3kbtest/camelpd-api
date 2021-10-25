CREATE TABLE IF NOT EXISTS users_table (
    id bigint PRIMARY KEY auto_increment,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
	username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS settings_table (
    settings_id bigint PRIMARY KEY auto_increment,
    user_id BIGINT,
    app_alerts BOOLEAN,
    email_notifications BOOLEAN,
    theme INTEGER,
    FOREIGN KEY (user_id)
        REFERENCES users_table(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS appointments_table (
    appointment_id BIGINT PRIMARY KEY auto_increment,
    user_id BIGINT,
    date DATE,
    start_time DATETIME,
    end_time DATETIME,
    description VARCHAR(255),
    appointment_notes VARCHAR(255),
    FOREIGN KEY (user_id)
        REFERENCES users_table(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tasks_table (
	task_id BIGINT PRIMARY KEY auto_increment,
    user_id BIGINT,
    done BOOLEAN,
    name VARCHAR(255),
    deadline DATETIME,
    progress INTEGER,
    priority INTEGER,
    FOREIGN KEY (user_id)
        REFERENCES users_table(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS journal_entries_table (
    entry_id BIGINT PRIMARY KEY auto_increment,
    user_id BIGINT,
    date DATE,
    summary TEXT,
    positives VARCHAR(255),
    mood_score INTEGER,
    FOREIGN KEY (user_id)
        REFERENCES users_table(id)
        ON DELETE CASCADE
);