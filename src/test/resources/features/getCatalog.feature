@catalog
Feature: Get Catalog

  Scenario: Get list of catalogs
    Given Authentication token is generated
    When Get catalog request is sent
    Then List of catalogs is received