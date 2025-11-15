INSERT INTO users (user_id, first_name, last_name, email, password, is_active)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'John', 'Doe', 'John.Doe', 'pass123', true),
    ('22222222-2222-2222-2222-222222222222', 'Alice', 'Smith', 'Alice.Smith', 'pass123', true),
    ('33333333-3333-3333-3333-333333333333', 'Bob', 'Brown', 'Bob.Brown', 'pass123', true);

INSERT INTO trainees (user_id, date_of_birth, address)
VALUES
    ('11111111-1111-1111-1111-111111111111', '1995-05-15', 'Tashkent, Uzbekistan'),
    ('33333333-3333-3333-3333-333333333333', '1990-10-20', 'New York, USA');

INSERT INTO training_types (training_type_id, training_type)
VALUES
    ('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Strength'),
    ('aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Cardio');

INSERT INTO trainers (user_id, training_type_id)
VALUES
    ('22222222-2222-2222-2222-222222222222', 'aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa');

INSERT INTO trainings (training_id, trainer_id, trainee_id, training_type_id, training_name, date, duration)
VALUES
    ('99999999-9999-9999-9999-999999999999', '22222222-2222-2222-2222-222222222222', "11111111-1111-1111-1111-111111111111",
     'aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Morning Workout', '2025-11-11 08:00:00', 90);
