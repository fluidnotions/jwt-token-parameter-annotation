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

    @GetMapping("/example")
    public void myRequest(@JwtTokenHeader(MyClaims.class) MyClaims claims) {
        // ...
    }
}
```
```


