package ch.fhnw.webec.booklist.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(
        title = "Booklist API",
        description = "An API for books and related entities.",
        version = "1.0.0"
    )
)
class OpenAPIConfiguration {}
