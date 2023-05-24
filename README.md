# Starling-Round-Up-Challenge
By Emily Fitzsimmons

This project implements a "round-up" feature for Starling customers using the public developer API. It allows customers to round up their transactions and transfer the rounded-up amount to a savings goal.

# Validate/Refresh the Access Token:
- Go to the Starling Bank Developers Account and refresh/generate a new access token.
- Create an application.properties file and add your access token. See "application.properties.example" for reference.

# Build & Run
To build the project, make sure you have Gradle installed. Run the following command in the project root directory:

**_gradle clean build_**

This command will run all tests.

You can run the application directly from Main.kt

The RoundupController exposes the round-up functionality as an API endpoint. It allows clients to make HTTP requests to perform the round-up calculation and transfer funds to the savings goal. The controller handles input validation, business logic execution, and error handling, providing a clear and standardized way to interact with the round-up feature.

# Further Improvements
Here are some potential areas for further improvement or expansion of the round-up feature:

- Swagger docs for the API endpoint, using Open API.
- Currently, the application uses an access token which expires every 24 hours. Preferably, the app would check the expiry time on the access token and use the refresh token to get new access tokens.
- Extensive testing: Due to time constraints, tests are very limited. Ideally the tests should aim to cover different scenarios, such as transactions with varying amounts and currencies.
- Error Handling: Enhance error handling to provide more detailed error messages and appropriate HTTP status codes in case of failures or invalid inputs.
- Logging: Implement comprehensive logging to facilitate troubleshooting and debugging.
- Automated Deployment: Configure automated deployment pipelines to streamline the deployment process.
- Monitoring & Metrics: Implement monitoring and metrics collection to gain insights into the usage and performance of the round-up feature.
