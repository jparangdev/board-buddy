#!/bin/bash

echo "🧪 Testing Test Data Reset API"
echo ""

BASE_URL="http://localhost:8080/api/test-data"

echo "1️⃣ Testing full reset..."
curl -X POST "$BASE_URL/reset" \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n\n"

echo "2️⃣ Testing sessions cleanup..."
curl -X DELETE "$BASE_URL/sessions" \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n\n"

echo "3️⃣ Testing groups cleanup..."
curl -X DELETE "$BASE_URL/groups" \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n\n"

echo "4️⃣ Testing temp users cleanup..."
curl -X DELETE "$BASE_URL/temp-users" \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n\n"

echo "✅ All tests completed!"
