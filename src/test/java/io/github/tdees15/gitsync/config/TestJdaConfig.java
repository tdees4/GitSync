package io.github.tdees15.gitsync.config;

import net.dv8tion.jda.api.JDA;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestJdaConfig {

    @Bean
    @Primary
    public JDA jda() {
        return mock(JDA.class);
    }

}
