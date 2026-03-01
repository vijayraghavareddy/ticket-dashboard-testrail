# Copilot Instructions

## Scope
These instructions apply to all AI-generated test case content in this repository.

## Goal
Whenever a Jira description is provided, generate TestRail-ready test cases using that Jira description as the primary source of truth.

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
