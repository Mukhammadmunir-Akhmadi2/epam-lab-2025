CREATE TABLE IF NOT EXISTS users (
    user_id UUID PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(18) NOT NULL,
    is_active BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS training_types (
    training_type_id UUID PRIMARY KEY,
    training_type VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS trainees (
    user_id UUID PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
    date_of_birth DATE,
    address VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS trainers (
    user_id UUID PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
    training_type_id UUID REFERENCES training_types(training_type_id)
);

CREATE TABLE IF NOT EXISTS trainee_trainer (
    trainee_id UUID NOT NULL REFERENCES trainees(user_id) ON DELETE CASCADE,
    trainer_id UUID NOT NULL REFERENCES trainers(user_id) ON DELETE CASCADE,
    PRIMARY KEY (trainer_id, trainee_id)
    );


CREATE TABLE IF NOT EXISTS trainings (
    training_id UUID PRIMARY KEY,
    trainer_id UUID NOT NULL REFERENCES trainers(user_id),
    trainee_id UUID NOT NULL REFERENCES trainees(user_id),
    training_type_id UUID NOT NULL REFERENCES training_types(training_type_id) ON DELETE RESTRICT,
    training_name VARCHAR(255) NOT NULL,
    date TIMESTAMP NOT NULL,
    duration INT NOT NULL
);

