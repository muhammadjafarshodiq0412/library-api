package com.jafarshodiq.library.config;

import com.jafarshodiq.library.constant.BaseConstant;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Library Service API")
                                .version("1.0.0")
                                .description(
                                        "REST API for managing library borrowers, books, and loans."
                                )
                                .contact(
                                        new Contact()
                                                .name("Muhammad Jafar Shodiq - Senior Java Developer")
                                                .email("jafarshodiq0412@gmail.com")
                                                .url(
                                                        "https://www.linkedin.com/in/jafar-shodiq-498354194/"
                                                )
                                )
                )
                .addServersItem(
                        new Server()
                                .url("http://localhost:8080/library-service")
                                .description("Local environment")
                );
    }

    /**
     * Customize Swagger parameters globally.
     *
     * <p>
     * This customizer:
     * <ul>
     *     <li>Adds X-Tracking-Ref header to every API</li>
     *     <li>Changes page documentation from zero-based to one-based</li>
     *     <li>Sets default page to 1</li>
     *     <li>Sets default page size to 10</li>
     *     <li>Changes sort from array<string> to string</li>
     *     <li>Sets default sort to createdAt,DESC</li>
     *     <li>Places X-Tracking-Ref before query parameters</li>
     * </ul>
     */
    @Bean
    public GlobalOpenApiCustomizer customizeSwaggerParameters() {

        return openAPI -> {

            if (openAPI.getPaths() == null) {
                return;
            }

            openAPI.getPaths()
                    .values()
                    .forEach(pathItem ->
                            pathItem.readOperations()
                                    .forEach(operation -> {

                                        List<Parameter> parameters =
                                                operation.getParameters();

                                        if (parameters == null) {
                                            parameters = new ArrayList<>();
                                        } else {
                                            parameters = new ArrayList<>(parameters);
                                        }

                                        /*
                                         * ==========================================
                                         * X-Tracking-Ref
                                         * ==========================================
                                         */
                                        parameters.removeIf(parameter ->
                                                BaseConstant.HeaderParameter.X_TRACKING_REF
                                                        .equals(parameter.getName())
                                                        && "header".equals(parameter.getIn())
                                        );

                                        Parameter trackingRefParameter =
                                                new Parameter()
                                                        .in("header")
                                                        .name(
                                                                BaseConstant.HeaderParameter.X_TRACKING_REF
                                                        )
                                                        .required(true)
                                                        .description(
                                                                "Client correlation ID used to trace "
                                                                        + "the request across the application."
                                                        )
                                                        .schema(
                                                                new StringSchema()
                                                        );

                                        /*
                                         * ==========================================
                                         * Pageable
                                         * ==========================================
                                         */
                                        parameters.forEach(parameter -> {

                                            if (!"query".equals(parameter.getIn())) {
                                                return;
                                            }

                                            switch (parameter.getName()) {

                                                case "page" -> {

                                                    parameter.setDescription(
                                                            "One-based page number (1...N)"
                                                    );

                                                    parameter.setSchema(
                                                            new IntegerSchema()
                                                                    ._default(1)
                                                    );

                                                    parameter.setExample(1);
                                                }

                                                case "size" -> {

                                                    parameter.setDescription(
                                                            "Number of records per page"
                                                    );

                                                    parameter.setSchema(
                                                            new IntegerSchema()
                                                                    ._default(10)
                                                    );

                                                    parameter.setExample(10);
                                                }

                                                case "sort" -> {

                                                    parameter.setDescription(
                                                            "Sorting criteria in the format: "
                                                                    + "property,(asc|desc). "
                                                                    + "Multiple sort criteria are supported."
                                                    );

                                                    /*
                                                     * Springdoc normally generates
                                                     * sort as array<string>.
                                                     *
                                                     * Change it to a simple String
                                                     * so Swagger UI does not show:
                                                     *
                                                     * Add string item
                                                     */
                                                    parameter.setSchema(
                                                            new StringSchema()
                                                                    ._default("createdAt,DESC")
                                                    );

                                                    parameter.setExample(
                                                            "createdAt,DESC"
                                                    );

                                                    parameter.setExplode(false);
                                                }

                                                default -> {
                                                    // No customization required
                                                }
                                            }
                                        });

                                        /*
                                         * ==========================================
                                         * Parameter ordering
                                         * ==========================================
                                         *
                                         * Header first
                                         * Then path parameters
                                         * Then query parameters
                                         */
                                        parameters.add(
                                                0,
                                                trackingRefParameter
                                        );

                                        parameters.sort(
                                                Comparator.comparingInt(
                                                        parameter -> {

                                                            if ("header".equals(
                                                                    parameter.getIn()
                                                            )) {
                                                                return 0;
                                                            }

                                                            if ("path".equals(
                                                                    parameter.getIn()
                                                            )) {
                                                                return 1;
                                                            }

                                                            if ("query".equals(
                                                                    parameter.getIn()
                                                            )) {
                                                                return 2;
                                                            }

                                                            return 3;
                                                        }
                                                )
                                        );

                                        operation.setParameters(parameters);
                                    })
                    );
        };
    }
}