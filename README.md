# Time Deposit Refactoring Kata - Take home assignment
## XA bank time deposit

## Context
A junior developer implemented some domain logic in a time deposit system but did not complete the API functionality. Your task is to refactor the existing codebase to implement all required functionalities based on the provided business requirements, ensuring no breaking changes occur.

## Requirements

1. **API Endpoint**:
  - Create a Restful API endpoint for updating the balances of all time deposits in the database.
  - Create a Restful API endpoint to get all time deposits.
    - Get API should return a list of all time deposits with this schema:
      - id
      - planType
      - balance
      - days
      - withdrawals

2. **Database Setup**:
  - Store all time deposit plans in a database.
  - Define the following tables:
    - `timeDeposits`:
      - `id`: Integer (primary key)
      - `planType`: String (required)
      - `days`: Integer (required)
      - `balance`: Decimal (required)
    - `withdrawals`:
      - `id`: Integer (primary key)
      - `timeDepositId`: Integer (Foreign Key, required)
      - `amount`: Decimal (required)
      - `date`: Date (required)

3. **Interest Calculation**:
  - Implement logic to calculate monthly interest based on the plan type:
    - **Basic plan**: 1% interest
    - **Student plan**: 3% interest (no interest after 1 year)
    - **Premium plan**: 5% interest (interest starts after 45 days)
  - No interest for the first 30 days for all plans.

4. **Refactoring Constraints**:
  - Do not introduce breaking changes to the shared `TimeDeposit` class or modify the `updateBalance` method signature.
  - Ensure your design is extensible to accommodate future complexities in interest calculations.

5. **Code Quality**:
  - Adhere to SOLID principles, design patterns, and clean code practices where applicable.

## Important Guidelines
- The existing `TimeDepositCalculator.updateBalance` method is functioning correctly. After refactoring, ensure that the behavior of this method remains unchanged.
- Your final solution should include **two API endpoints**. Do not develop additional endpoints.
- **Do not** create a pull request or a new branch in the ikigai-digital repository. Instead, fork this repository into your own GitHub repository and develop the solution there.
- No need to handle invalid input / exceptions.
- You can use any tools, frameworks, libraries, etc. that you find suitable.
- In case of ambiguity, make logical assumptions and justify them in your code comments.

## Required Stack
- Use OpenAPI swagger contract
- Embrace Hexagonal Architecture
- Atomic commits
- Test containers

## Submission Instructions
- Include clear instructions on how to run the application.
- Email the link to your public GitHub repository.
