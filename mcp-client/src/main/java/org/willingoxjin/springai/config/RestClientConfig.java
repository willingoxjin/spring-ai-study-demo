package org.willingoxjin.springai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 *
 * @author Jin.Nie
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient webClient() {
        return RestClient.builder().build();
    }

}
