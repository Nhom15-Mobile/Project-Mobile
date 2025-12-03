#!/bin/bash

# Comprehensive API Test Script
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjbWlscHR3dWEwMDAwbmp1czNrd3k5cmFqIiwicm9sZSI6IlBBVElFTlQiLCJpYXQiOjE3NjQ1MTMyMDIsImV4cCI6MTc2NTExODAwMn0.9qupH31fnlsCcguWW6BCHhAXLu0xVnPzq9xdfG94T1w"
API="http://localhost:4000/api"

echo "🚀 COMPREHENSIVE API TEST"
echo ""

# Test 1: Get Current User
echo "✅ Test 1: Get Current User (/users/me)"
curl -s "$API/users/me" -H "Authorization: Bearer $TOKEN" | jq -r '.data | "\(.id) - \(.fullName) (\(.role))"'
echo ""

# Test 2: Get All Users
echo "✅ Test 2: Get All Users (/users)"
curl -s "$API/users" -H "Authorization: Bearer $TOKEN" | jq -r '.data | length' | xargs echo "Total users:"
echo ""

# Test 3: Get Doctors
echo "✅ Test 3: Get Doctors (/doctors)"
curl -s "$API/doctors" -H "Authorization: Bearer $TOKEN" | jq -r '.data | length' | xargs echo "Total doctors:"
echo ""

# Test 4: Create Care Profile
echo "✅ Test 4: Create Care Profile (/care-profiles)"
CARE=$(curl -s -X POST "$API/care-profiles" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "firstName": "Jane",
    "lastName": "Doe",
    "relation": "Spouse",
    "dob": "1992-05-20",
    "gender": "FEMALE",
    "phone": "0912345678"
  }')
CARE_ID=$(echo "$CARE" | jq -r '.data.id' 2>/dev/null)
echo "Created: $CARE_ID"
echo ""

# Test 5: Get Care Profiles
echo "✅ Test 5: Get Care Profiles (/care-profiles)"
curl -s "$API/care-profiles" -H "Authorization: Bearer $TOKEN" | jq -r '.data | length' | xargs echo "Total care profiles:"
echo ""

# Test 6: Get Locations
echo "✅ Test 6: Get Provinces (/locations/provinces)"
curl -s "$API/locations/provinces" -H "Authorization: Bearer $TOKEN" | jq -r 'length' | xargs echo "Total provinces:"
echo ""

# Test 7: Get Notifications
echo "✅ Test 7: Get Notifications (/notifications)"
curl -s "$API/notifications" -H "Authorization: Bearer $TOKEN" | jq -r '.data | length' | xargs echo "Total notifications:"
echo ""

echo "🎉 ALL TESTS COMPLETED!"
