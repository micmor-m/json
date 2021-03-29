# JAVA Server API

JAVA Server API is a single route API "GET / " that expects to receive by URI parameters two numbers and an operation.
The server sends back the result in JSON format.

## Development environment

JAVA Server API has been developed and tested using the following:
- OpenJDK Corretto11
- org.json:json:20180813

## Documentation

### Behaviour

- When a client makes a GET request to the route "/" with the correct parameters format:
```
 GET /?leftOperand=[val1]&rightOperand=[val2]&operation=[operationSymbol]
```

the Server will respond and send back the result in JSON format with the following structure:

```
    {
        "Expression": val1 operationSymbol val2,
        "Result": result
    }  
```

- If a different route is requested, the server will return an empty JSON string;
- If one or more of the required parameter are missing, the server will return an empty JSON string;
- If one or more of the parameters are not parsable to number, the server will return an empty JSON string;