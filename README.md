# SWIFT Code Management API

## Overview
This API provides functionalities to manage SWIFT codes for banks and their branches. The API allows retrieving details about SWIFT codes, listing SWIFT codes by country, adding new SWIFT entries, and deleting SWIFT codes. The application is built using **Spring Boot** and **PostgreSQL** and can be easily deployed using Docker.

## Features
- Retrieve details for a specific SWIFT code.
- Fetch all SWIFT codes for a given country.
- Add new SWIFT codes.
- Delete SWIFT codes from the database.

## Running the Application with Docker

### Prerequisites
Ensure you have installed:
- [Docker](https://www.docker.com/get-started)
- [Docker Compose](https://docs.docker.com/compose/install/)

### Steps to Run
1. Clone this repository:
   ```sh
   git clone https://github.com/Dorajn/SWIFT-codes-REST-API.git
   ```
2. Build the application Docker image:
   ```sh
   docker build -t swift-code-api .
   ```
3. Start the application along with PostgreSQL using Docker Compose:
   ```sh
   docker-compose up -d
   ```
4. The API will be available at:
   ```
   http://localhost:8080
   ```

## API Endpoints

### 1. Retrieve details of a SWIFT code
**GET** `/v1/swift-codes/{swift-code}`

**Response for a headquarters SWIFT code:**
```json
{
    "address": "string",
    "bankName": "string",
    "countryISO2": "string",
    "countryName": "string",
    "isHeadquarter": true,
    "swiftCode": "string",
    "branches": [
        {
            "address": "string",
            "bankName": "string",
            "countryISO2": "string",
            "isHeadquarter": false,
            "swiftCode": "string"
        }
    ]
}
```

**Response for a branch SWIFT code:**
```json
{
    "address": "string",
    "bankName": "string",
    "countryISO2": "string",
    "countryName": "string",
    "isHeadquarter": false,
    "swiftCode": "string"
}
```

### 2. Retrieve all SWIFT codes for a country
**GET** `/v1/swift-codes/country/{countryISO2code}`

**Response:**
```json
{
    "countryISO2": "string",
    "countryName": "string",
    "swiftCodes": [
        {
            "address": "string",
            "bankName": "string",
            "countryISO2": "string",
            "isHeadquarter": true,
            "swiftCode": "string"
        },
        {
            "address": "string",
            "bankName": "string",
            "countryISO2": "string",
            "isHeadquarter": false,
            "swiftCode": "string"
        }
    ]
}
```

### 3. Add a new SWIFT code
**POST** `/v1/swift-codes`

**Request Body:**
```json
{
    "address": "string",
    "bankName": "string",
    "countryISO2": "string",
    "countryName": "string",
    "isHeadquarter": true,
    "swiftCode": "string"
}
```

**Response:**
```json
{
    "message": "SWIFT code added successfully."
}
```

### 4. Delete a SWIFT code
**DELETE** `/v1/swift-codes/{swift-code}`

**Response:**
```json
{
    "message": "SWIFT code deleted successfully."
}
```