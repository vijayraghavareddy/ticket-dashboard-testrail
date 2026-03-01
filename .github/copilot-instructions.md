# Copilot Instructions

## Scope
These instructions apply to all AI-generated test case content and test automation in this repository.

## Goals
1. **Test Case Creation**: Generate TestRail-ready test cases from Jira descriptions as the primary source of truth.
2. **Test Automation**: Pull test cases from TestRail and automate them using Rest Assured + Cucumber framework.
3. **Traceability**: Maintain complete traceability from Jira → TestRail → Automated Tests.

## Mandatory Rules
1. Always treat the TestRail project as: **ticket-dashboard**.
2. Always map the Jira description into TestRail test case content.
3. Always format the generated test case using the TestRail structure shown in the reference image.
4. Do not invent requirements that are not present in Jira. If details are missing, add explicit assumptions in a short "Assumptions" subsection.
5. Keep wording implementation-agnostic and test-focused.
6. Prefer API-test language and validation detail when the Jira description implies API behavior.

## Required Test Case Output Format
For every generated test case, use the following sections in this exact order:

1. **Title**
2. **Type** (default: `Manual (To Be Automated)` unless otherwise specified)
3. **Priority** (derive from Jira if present; otherwise set to `4 - Critical` only if business-critical behavior is explicit, else `Medium`)
4. **References** (include Jira key/link)
5. **Automation Status** (`TO BE AUTOMATED` unless explicitly requested otherwise)
6. **Test Description**
7. **Preconditions**
8. **Execution Steps**
9. **Expected Result**
10. **Steps** (step-by-step table style with paired expected result per step)

## Formatting Template (Use Every Time)
Use this template exactly and fill placeholders from Jira:

### Title
<Concise behavior-focused title>

### Type
Manual (To Be Automated)

### Priority
<Priority from Jira or inferred>

### References
<JIRA-KEY or Jira URL>

### Automation Status
TO BE AUTOMATED

### Test Description
<One paragraph describing what is validated and success criteria>

### Preconditions
- <Environment/service availability>
- <Auth/roles/headers/tokens needed>
- <Data prerequisites>

### Execution Steps
1. <Action 1>
2. <Action 2>
3. <Action 3>

### Expected Result
- <HTTP status or UI/system outcome>
- <Response/body/state validations>
- <Persistence/side-effect validations>

### Steps
| Step | Action | Expected Result |
|---|---|---|
| 1 | <Detailed action> | <Expected result for step 1> |
| 2 | <Detailed action> | <Expected result for step 2> |
| 3 | <Detailed action> | <Expected result for step 3> |

## Jira-to-Test Mapping Guidance
- Convert acceptance criteria into explicit assertions.
- Include positive path first; add edge/negative paths when Jira mentions constraints or failure modes.
- For async behavior, validate both initial acknowledgment and eventual completion states.
- If API endpoint details are present, include method, endpoint, required headers, and key payload fields in steps.

## Missing Information Handling
If Jira description is incomplete, include:

### Assumptions
- <Assumption 1>
- <Assumption 2>

Then proceed with the test case using the safest, minimal assumptions.

## Consistency Requirement
Every time test cases are requested from Jira input, the response must:
- Reference project name: **ticket-dashboard**
- Follow the section order above
- Include both high-level sections and the step-wise table
- Stay aligned with the TestRail style from the provided reference image

---

# Test Automation Instructions

## Test Automation Framework
This project uses:
- **Rest Assured 5.5.0** for REST API testing
- **Cucumber 7.20.1** for BDD test scenarios
- **JUnit 5** for test execution
- **Maven** for build and dependency management

## TestRail Integration Workflow

### 1. Pulling Test Cases from TestRail
When pulling test cases from TestRail, follow these steps:

1. **Connect to TestRail**: Use TestRail MCP tools to access the project
   - Project: **ticket-dashboard** (ID: 2)
   - Default Suite: **Master**
   - Default Section: **Integrations**

2. **Retrieve Test Cases**: Use `mcp_testrail_getCases` and `mcp_testrail_getCase` 
   - Pull high-level case list first
   - Retrieve detailed content for each case including custom fields

3. **Extract Key Information**:
   - Title and TestRail case ID
   - References (Jira story/epic)
   - Priority and type
   - Preconditions (from `custom_preconds`)
   - Steps (from `custom_steps`)
   - Expected results (from `custom_expected`)

### 2. Creating Cucumber Feature Files

#### File Naming Convention
- Use lowercase with hyphens: `feature-name.feature`
- Place in: `src/test/resources/features/`
- Group related scenarios in one feature file

#### Feature File Structure
```gherkin
@StoryTag @CategoryTag
Feature: Clear feature description
  Background context explaining the API/feature being tested

  Background:
    Given the API base URL is configured
    And required authentication headers are available

  @Positive @TC{id}
  Scenario: Clear scenario title
    Given precondition setup
    When action is performed
    Then expected outcome is verified
    And additional validations

  @Negative @SubCategory @TC{id}
  Scenario Outline: Data-driven scenario title
    Given precondition with "<parameter>"
    When action with "<parameter>"
    Then validation with expected outcome
    
    Examples:
      | parameter |
      | value1    |
      | value2    |
```

#### Tag Management Rules
1. **Story/Feature Tags**: `@TD-{number}` for Jira story reference
2. **Category Tags**: `@IntegrationsAPI`, `@Positive`, `@Negative`, `@Security`
3. **Sub-Category Tags**: `@Authentication`, `@Authorization`, `@Validation`, `@Resilience`, `@NotFound`, `@MethodNotAllowed`
4. **Test Case Tags**: `@TC{TestRailID}` for individual test case traceability
5. **Always include at least**: Story tag, Category tag, and Test Case tag

### 3. Implementing Step Definitions

#### Step Definition Guidelines
Place all step definitions in: `src/test/java/com/example/apitests/steps/ApiStepDefinitions.java`

**Use Rest Assured fluent API pattern**:
```java
@When("I send a GET request to {string} with valid authentication")
public void iSendAGetRequestToWithValidAuthentication(String endpoint) {
    response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", authToken)
            .when()
            .get(endpoint)
            .then()
            .extract()
            .response();
}
```

**Common Step Patterns**:

1. **Setup Steps** (Given):
   - Environment/configuration setup
   - Test data preparation
   - Authentication state management
   - Precondition verification

2. **Action Steps** (When):
   - HTTP requests (GET, POST, PUT, DELETE, PATCH)
   - Include authentication variants (with/without, sufficient/insufficient)
   - Support for headers, query params, body

3. **Validation Steps** (Then/And):
   - Status code validation (specific and ranges)
   - Response body validation (schema, fields, values)
   - Error message validation
   - Security validation (no data leakage)
   - State verification (no mutations where expected)

**Assertion Best Practices**:
```java
// Always include descriptive messages
assertThat("Response status code mismatch", 
        response.getStatusCode(), equalTo(expectedStatusCode));

// Use Hamcrest matchers: equalTo, notNullValue, containsString, greaterThan, etc.
assertThat("Response body is empty", responseBody, notNullValue());
assertThat("Error should indicate forbidden", 
        responseBody, containsStringIgnoringCase("forbidden"));
```

### 4. Configuration Management

#### config.properties Structure
Organize configuration by purpose:

```properties
# Base API Configuration
base.url=https://default-api-url

# Feature-Specific Configuration
testrail.integration.base.url=http://localhost:8080
testrail.integration.endpoint=/integrations/testrail/run-status

# Authentication
api.auth.token=Bearer valid-token
api.auth.insufficient.token=Bearer low-privilege-token

# Test Data
test.valid.ticket.id=VALID-123
test.unknown.ticket.id=UNKNOWN-999
test.malformed.ticket.id.1=@#$%^&*

# Endpoints
health.check.endpoint=/health
```

#### Using ConfigManager
```java
// Get property with default
String baseUrl = ConfigManager.getProperty("testrail.integration.base.url", "http://localhost:8080");

// Get property (returns null if not found)
String token = ConfigManager.getProperty("api.auth.token");
```

### 5. Test Scenario Design

#### Coverage Requirements
For each API endpoint, create scenarios covering:

1. **Happy Path** (Positive):
   - Valid input → Expected success response
   - All required fields populated correctly
   - Valid authentication and authorization

2. **Error Handling** (Negative):
   - Not Found (404): Resource doesn't exist
   - Bad Request (400): Invalid/malformed input
   - Upstream Failures (5xx): Dependency unavailable

3. **Security** (Negative):
   - Unauthorized (401): Missing/invalid authentication
   - Forbidden (403): Insufficient permissions
   - Method Not Allowed (405): Unsupported HTTP methods
   - Data Leakage: Ensure errors don't expose sensitive data

4. **Resilience**:
   - Graceful degradation when dependencies fail
   - No sensitive information in error messages
   - System remains available for subsequent requests

#### Scenario Outline Usage
Use Scenario Outline for:
- Testing multiple malformed inputs
- Testing multiple HTTP methods
- Testing boundary conditions
- Data-driven validation

```gherkin
Scenario Outline: Validate input constraints
  Given a ticket ID "<ticket_id>" is prepared
  When I send a GET request to "/endpoint/<ticket_id>"
  Then the response status code should be <status>
  
  Examples:
    | ticket_id | status |
    | valid-123 | 200    |
    | @#$%^&*   | 400    |
    |           | 400    |
```

### 6. Documentation Standards

When automating TestRail test cases, create/update:

1. **Feature File Comments**: Include TestRail case reference in scenario description
2. **TESTCASE_MAPPING.md**: Map TestRail IDs to scenarios with execution commands
3. **README.md**: Update with new feature coverage and tag info
4. **Step Definition Comments**: Document complex validation logic

### 7. Test Execution Guidelines

#### Running Tests
```bash
# All tests
mvn clean test

# By feature/story
mvn test -Dcucumber.filter.tags="@TD-1"

# By category
mvn test -Dcucumber.filter.tags="@Positive"
mvn test -Dcucumber.filter.tags="@Security"

# Specific test case
mvn test -Dcucumber.filter.tags="@TC42"

# Combined tags
mvn test -Dcucumber.filter.tags="@TD-1 and @Positive"
mvn test -Dcucumber.filter.tags="@Security and not @MethodNotAllowed"
```

#### Reporting
All tests generate:
- **Console**: Pretty-formatted real-time output
- **HTML**: `target/cucumber-reports/cucumber.html`
- **JSON**: `target/cucumber-reports/cucumber.json`
- **JUnit XML**: `target/surefire-reports/`

### 8. Traceability Maintenance

Maintain complete traceability chain:

```
Jira Story → TestRail Test Case → Cucumber Scenario → Step Definition → Test Execution
```

**Traceability Indicators**:
- Jira story in feature tag: `@TD-1`
- TestRail case in scenario tag: `@TC42`
- TestRail reference in scenario description
- Jira key in `refs` field when viewing in TestRail
- Test results can be pushed back to TestRail via API

### 9. Automation Checklist

When automating a TestRail test case:

- [ ] Pull complete test case details from TestRail
- [ ] Create/update feature file with appropriate tags
- [ ] Implement step definitions (or verify existing ones work)
- [ ] Add test data to config.properties
- [ ] Verify compilation: `mvn test-compile`
- [ ] Run the specific test: `mvn test -Dcucumber.filter.tags="@TC{id}"`
- [ ] Update TESTCASE_MAPPING.md with new automation
- [ ] Verify all assertions match TestRail expected results
- [ ] Check that error scenarios don't expose sensitive data

### 10. Naming Conventions

**Feature Files**: 
- `feature-name.feature` (lowercase, hyphens)
- Example: `testrail-integration.feature`, `user-management.feature`

**Step Definitions**:
- Descriptive method names: `iSendAGetRequestToWithValidAuthentication`
- Parameters in quotes match Cucumber expressions: `{string}`, `{int}`

**Test Data**:
- Config keys: `feature.purpose.detail`
- Example: `test.valid.ticket.id`, `api.auth.token`

**Tags**:
- Story: `@TD-{number}`
- Category: `@CamelCase` (e.g., `@IntegrationsAPI`)
- Test Case: `@TC{number}`

### 11. Best Practices

1. **Reuse Step Definitions**: Before creating new steps, check if existing ones can be reused
2. **Descriptive Assertions**: Always include meaningful assertion messages
3. **No Hardcoding**: Use config.properties for URLs, tokens, test data
4. **Security First**: Always verify no data leakage in error responses
5. **Clear Scenarios**: Each scenario should test ONE specific behavior
6. **Background for Common Setup**: Use Background for steps repeated in all scenarios
7. **Examples for Variations**: Use Scenario Outline for testing multiple similar cases
8. **Tag Consistently**: Apply all relevant tags for flexible test execution
9. **Document Assumptions**: When TestRail case is unclear, add comments explaining interpretation
10. **Keep Tests Independent**: Each scenario should be runnable in isolation

### 12. Common Patterns

#### Authentication Variants
```java
// With valid auth
response = given()
    .header("Authorization", validToken)
    .when().get(endpoint);

// Without auth
response = given()
    .when().get(endpoint);  // No auth header

// With insufficient permissions
response = given()
    .header("Authorization", lowPrivilegeToken)
    .when().get(endpoint);
```

#### Status Code Validation
```java
// Specific status
assertThat(response.getStatusCode(), equalTo(200));

// Range of acceptable statuses
assertThat(response.getStatusCode(), isIn(Arrays.asList(502, 503, 504)));

// Using helper method
List<Integer> expectedCodes = parseStatusCodes("502,503,504");
assertThat(response.getStatusCode(), in(expectedCodes));
```

#### Response Validation
```java
// Schema validation
assertThat(response.jsonPath().get("ticket_id"), notNullValue());
assertThat(response.jsonPath().get("run_status"), notNullValue());

// Value matching
String ticketId = response.jsonPath().getString("ticket_id");
assertThat(ticketId, equalTo("VALID-123"));

// Error message validation
String responseBody = response.getBody().asString();
assertThat(responseBody, containsStringIgnoringCase("not found"));
```

---

## Summary

When working in this repository:
1. **Test Case Creation**: Use Jira as source → Generate TestRail-formatted test cases
2. **Test Automation**: Pull from TestRail → Create Cucumber scenarios → Implement Rest Assured steps
3. **Maintain Traceability**: Jira ↔ TestRail ↔ Automated Tests via tags and references
4. **Follow Patterns**: Use established patterns for step definitions, configuration, and validation
5. **Document Thoroughly**: Update mapping docs, README, and maintain clear comments
