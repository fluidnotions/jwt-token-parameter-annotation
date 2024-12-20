* cybicom fork
# Spring Boot JWT Token Decoder Library

This library provides a custom annotation `@JwtTokenHeader` that can be used to decode a JWT token JSON and convert it to a specified class using `ObjectMapper` in a Spring Boot application.

## Features
- Custom annotation `@JwtTokenHeader` for injecting decoded JWT token JSON as a specified class.
- Custom `HandlerMethodArgumentResolver` to handle decoding and conversion logic.
- Seamless integration with Spring Boot applications, provided `WebMvcConfigurer` is present in the project.

## Usage
1. Add the library to your Spring Boot project.
2. Ensure that your project has a `WebMvcConfigurer` to register the custom argument resolver.
3. Annotate the method parameter with `@JwtTokenHeader` where you want to inject the decoded JWT token JSON.
4. Implement the logic to handle the converted object in the annotated method.

Example:
```java
@RestController
public class MyController {

    record MyClaims(String sub, String name, long iat) {}

    @GetMapping("/example")
    public void myRequest(@JwtTokenHeader(MyClaims.class) MyClaims claims) {
        // ...
    }
}
```
```shell
curl --request GET \
  --url http://localhost:8080/hello \
  --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c' 
```

## Demo
[Spring Boot JWT Token Decoder Demo](https://github.com/fluidnotions/demo-jwt-token-parameter-annotation) - A simple Spring Boot application demonstrating the usage of the library.


