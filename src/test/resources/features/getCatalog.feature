@catalog
Feature: Get Catalog

  Scenario: Get list of catalogs
    Given Authentication token is generated
    When Get catalog request is sent
    Then List of catalogs is received

  Scenario: Catalogs cannot be received without authorization
    When Get catalog request is sent without authorization
    Then Unauthorized error is received