package org.willingoxjin.springai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 *
 * @author Jin.Nie
 */
@SpringBootApplication
public class McpClientApplication {

    public static void main(String[] args) {
        LoadEnvHelper.loadEnv();
        // new SpringApplicationBuilder(McpClientApplication.class)
        //         .web(WebApplicationType.SERVLET)
        //         .run(args);
        SpringApplication.run(McpClientApplication.class, args);
    }

}
