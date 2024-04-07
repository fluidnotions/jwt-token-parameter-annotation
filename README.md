# Spring Boot JWT Token Decoder Library

This library provides a custom annotation `@JwtTokenHeader` that can be used to decode a JWT token JSON and convert it to a specified class using `ObjectMapper` and AOP in a Spring Boot application.

## Features
- Custom annotation `@JwtTokenHeader` for injecting decoded JWT token JSON as a specified class.
- Aspect-oriented programming (AOP) implementation to handle decoding and conversion logic.
- Seamless integration with Spring Boot applications.

## Usage
1. Add the library to your Spring Boot project.
2. Annotate the method parameter with `@JwtTokenHeader` where you want to inject the decoded JWT token JSON.
3. Implement the logic to handle the converted object in the annotated method.

Example:
```java
@RestController
public class MyController {

    @GetMapping("/example")
    public void myRequest(@JwtTokenHeader(MyClaims.class) MyClaims claims) {
        // ...
    }
}
```


