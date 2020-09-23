package com.rbkmoney.partyshop;

import com.rbkmoney.easyway.EnvironmentProperties;
import com.rbkmoney.easyway.TestContainers;
import com.rbkmoney.easyway.TestContainersBuilder;
import com.rbkmoney.easyway.TestContainersParameters;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.ClassRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.FailureDetectingExternalResource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.function.Consumer;

@Slf4j
@DirtiesContext
@RunWith(SpringRunner.class)
@EnableConfigurationProperties({DataSourceProperties.class})
@ContextConfiguration(classes = {DataSourceAutoConfiguration.class}, initializers = PostgresAbstractTest.Initializer.class)
public abstract class PostgresAbstractTest {

    private static final TestContainers POSTGRES = TestContainersBuilder.builderWithTestContainers(TestContainersParameters::new)
            .addPostgresqlTestContainer()
            .build();

    @ClassRule
    public static final FailureDetectingExternalResource resource = new FailureDetectingExternalResource() {

        @Override
        protected void starting(Description description) {
            POSTGRES.startTestContainers();
        }

        @Override
        protected void failed(Throwable e, Description description) {
            log.warn("Test Container start failed ", e);
        }

        @Override
        protected void finished(Description description) {
            POSTGRES.stopTestContainers();
        }
    };

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues
                    .of(POSTGRES.getEnvironmentProperties(getEnvironmentPropertiesConsumer()))
                    .applyTo(configurableApplicationContext);
        }
    }

    private static Consumer<EnvironmentProperties> getEnvironmentPropertiesConsumer() {
        return environmentProperties -> {
            PostgreSQLContainer postgreSQLContainer = POSTGRES.getPostgresqlTestContainer().get();
            environmentProperties.put("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
            environmentProperties.put("spring.datasource.username", postgreSQLContainer.getUsername());
            environmentProperties.put("spring.datasource.password", postgreSQLContainer.getPassword());
            environmentProperties.put("spring.flyway.url", postgreSQLContainer.getJdbcUrl());
            environmentProperties.put("spring.flyway.user", postgreSQLContainer.getUsername());
            environmentProperties.put("spring.flyway.password", postgreSQLContainer.getPassword());
        };
    }
}
