# API ENDPOINTS

### CURRENCY CONVERTER ERRORS

> That is the list example of invocations
>
> localhost:8080/api/v1/currency_converter/convert
>

```
curl --location 'localhost:8080/api/v1/currency_converter/convert' \
--header 'Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.EY26NYcY2Syqu9K0_IvOcl02SzPampgz3nayllBJ9RQ' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=D9E47F55F6FCEE0EEC0B91080FEFAACF' \
--data '{
    "inputCurrency": "CAD",
    "outputCurrency": "CAD",
    "amount": 1
}'
```