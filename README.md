# Starling-Round-Up-Challenge
This project implements a "round-up" feature for Starling customers using the public developer API. It allows customers to round up their transactions and transfer the rounded-up amount to a savings goal.

# Validate/Refresh the Access Token:
- Go to the Starling Bank Developers Account and refresh/generate a new access token.
- Replace the access token on file "application.properties" in the bearerToken

# Build & Run
To build the project, make sure you have Gradle installed. Run the following command in the project root directory:

gradle clean build

This command will run all tests.

You can run the application directly from Main.kt

# Further Improvements
Here are some potential areas for further improvement or expansion of the round-up feature:

Error Handling: Enhance error handling to provide more detailed error messages and appropriate HTTP status codes in case of failures or invalid inputs.
Logging: Implement comprehensive logging to facilitate troubleshooting and debugging.
Automated Deployment: Configure automated deployment pipelines to streamline the deployment process.
Monitoring & Metrics: Implement monitoring and metrics collection to gain insights into the usage and performance of the round-up feature.
These improvements can be implemented based on the specific requirements and needs of the project.
