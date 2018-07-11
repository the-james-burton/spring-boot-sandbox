package com.jimsey.springbootsandbox;

import com.jimsey.springbootsandbox.services.TextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Flux;

import java.lang.invoke.MethodHandles;
import java.time.Duration;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
public class SpringBootSandboxApplication {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    /**
     * Starting point for this application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(SpringBootSandboxApplication.class)
                .initializers((ApplicationContextInitializer<GenericApplicationContext>) SpringBootSandboxApplication::registerBeans)
                .run(args);
    }

    /**
     * Programmatically register our beans. This is a new functional alternative to
     * using annotations that should allow more configuration and more assistance in the IDE.
     *
     * @param ctx spring application context
     */
    private static void registerBeans(GenericApplicationContext ctx) {
        ctx.registerBean(TextService.class);
        ctx.registerBean(SecurityWebFilterChain.class,
                () -> configureWebSecurity());
        ctx.registerBean(RouterFunction.class,
                () -> route(GET("/hi"), req -> ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(Flux.just("one", "two", "three", "four").delayElements(Duration.ofSeconds(1)), String.class)));
        ctx.registerBean(ApplicationRunner.class,
                () -> args1 -> log.info(ctx.getBean(TextService.class).generateText()));
    }

    /**
     * Configure web security for this application, which for now is none
     *
     * @return a configured spring security object
     */
    private static SecurityWebFilterChain configureWebSecurity() {
        ServerHttpSecurity security = ServerHttpSecurity.http();
        security.csrf().disable()
                .authorizeExchange().anyExchange().permitAll();
        return security.build();
    }
}
