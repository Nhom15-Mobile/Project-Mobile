#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

API_URL="http://localhost:3000/api"
TOKEN=""

echo -e "${YELLOW}=== API TEST SCRIPT ===${NC}\n"

# 1. Health Check
echo -e "${YELLOW}1. Health Check${NC}"
curl -s -X GET "$API_URL/health" | jq .
echo -e "\n"

# 2. Register User
echo -e "${YELLOW}2. Register User${NC}"
REGISTER_RESPONSE=$(curl -s -X POST "$API_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser'$(date +%s)'@example.com",
    "password": "Test@123456",
    "fullName": "Test User"
  }')
echo "$REGISTER_RESPONSE" | jq .
echo -e "\n"

# 3. Login User
echo -e "${YELLOW}3. Login User${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "$API_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser'$(date +%s)'@example.com",
    "password": "Test@123456"
  }')
echo "$LOGIN_RESPONSE" | jq .

# Extract token
TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.data.token' 2>/dev/null)
if [ "$TOKEN" != "null" ] && [ -n "$TOKEN" ]; then
  echo -e "${GREEN}Token: $TOKEN${NC}\n"
else
  echo -e "${RED}Failed to get token. Using test token...${NC}\n"
  TOKEN="test_token"
fi

# 4. Get Current User Profile
echo -e "${YELLOW}4. Get Current User Profile${NC}"
curl -s -X GET "$API_URL/users/me" \
  -H "Authorization: Bearer $TOKEN" | jq .
echo -e "\n"

# 5. Get All Users
echo -e "${YELLOW}5. Get All Users${NC}"
curl -s -X GET "$API_URL/users" \
  -H "Authorization: Bearer $TOKEN" | jq .
echo -e "\n"

# 6. Create Patient
echo -e "${YELLOW}6. Create Patient${NC}"
PATIENT_RESPONSE=$(curl -s -X POST "$API_URL/patient" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john'$(date +%s)'@example.com",
    "phone": "0123456789",
    "dateOfBirth": "1990-01-01",
    "gender": "MALE",
    "address": "123 Main St"
  }')
echo "$PATIENT_RESPONSE" | jq .
PATIENT_ID=$(echo "$PATIENT_RESPONSE" | jq -r '.data.id' 2>/dev/null)
echo -e "\n"

# 7. Get All Patients
echo -e "${YELLOW}7. Get All Patients${NC}"
curl -s -X GET "$API_URL/patient" \
  -H "Authorization: Bearer $TOKEN" | jq .
echo -e "\n"

# 8. Create Doctor
echo -e "${YELLOW}8. Create Doctor${NC}"
DOCTOR_RESPONSE=$(curl -s -X POST "$API_URL/doctors" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "firstName": "Dr",
    "lastName": "Smith",
    "email": "doctor'$(date +%s)'@example.com",
    "specialization": "Cardiology",
    "licenseNumber": "LIC'$(date +%s)'",
    "phone": "0987654321"
  }')
echo "$DOCTOR_RESPONSE" | jq .
DOCTOR_ID=$(echo "$DOCTOR_RESPONSE" | jq -r '.data.id' 2>/dev/null)
echo -e "\n"

# 9. Get All Doctors
echo -e "${YELLOW}9. Get All Doctors${NC}"
curl -s -X GET "$API_URL/doctors" \
  -H "Authorization: Bearer $TOKEN" | jq .
echo -e "\n"

# 10. Get All Locations
echo -e "${YELLOW}10. Get Provinces (Locations)${NC}"
curl -s -X GET "$API_URL/locations/provinces" \
  -H "Authorization: Bearer $TOKEN" | jq . | head -20
echo -e "\n"

# 11. Get All Notifications
echo -e "${YELLOW}11. Get All Notifications${NC}"
curl -s -X GET "$API_URL/notifications" \
  -H "Authorization: Bearer $TOKEN" | jq .
echo -e "\n"

# 12. Get Care Profiles
echo -e "${YELLOW}12. Get Care Profiles${NC}"
curl -s -X GET "$API_URL/care-profiles" \
  -H "Authorization: Bearer $TOKEN" | jq .
echo -e "\n"

echo -e "${GREEN}=== TEST COMPLETED ===${NC}"
