@training @positive
Feature: Training workload integration

  @positive @workload
  Scenario: Training creation updates trainer workload
    And a trainer exists
    And a trainee exists
    And trainee is authenticated
    When trainee creates training with trainer
    Then trainer workload should increase by 60 minutes

  @positive @workload
  Scenario: Deleting trainee decreases workload
    And a trainer exists
    And a trainee exists
    And trainee is authenticated
    When trainee creates training with trainer
    And trainee deletes account
    Then trainer workload should be 0

  @negative @workload
  Scenario: Creating training with non-existing trainer
    Given a trainee exists
    And trainee is authenticated
    When trainee creates training with non existing trainer
    Then request should fail with status 404

  @negative @workload
  Scenario: Training date cannot be in the past
    Given a trainer exists
    And a trainee exists
    And trainee is authenticated
    When trainee creates training with date in the past
    Then request should fail with status 400

  @negative @nfr @auth @workload
  Scenario: Unauthenticated trainee cannot create training
    Given a trainer exists
    And a trainee exists
    When trainee creates training without authentication
    Then request should fail with status 401

  @negative @workload
  Scenario: Training with zero duration is rejected
    Given a trainer exists
    And a trainee exists
    And trainee is authenticated
    When trainee creates training with zero duration
    Then request should fail with status 400

  @positive @workload
  Scenario: Multiple trainings accumulate workload correctly
    Given a trainer exists
    And a trainee exists
    And trainee is authenticated
    When trainee creates 3 trainings with 60 minutes each
    Then trainer workload should increase by 180 minutes

  @negative @workload @auth
  Scenario: Trainee cannot create training for another trainee's session
    Given a trainer exists
    And a trainee exists
    And another trainee exists
    And trainee is authenticated
    When trainee creates training using another trainee username
    Then request should fail with status 401

  @negative @workload
  Scenario: Trainee cannot create training when trainer has overlapping schedule
    Given a trainer exists
    And a trainee exists
    And trainee is authenticated
    When trainee creates training with trainer
    When trainee creates another training at the same time with the same trainer
    Then request should fail with status 409