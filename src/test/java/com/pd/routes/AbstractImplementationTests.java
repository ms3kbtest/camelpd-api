package com.pd.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.component.mock.MockComponent;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class AbstractImplementationTests extends CamelTestSupport {

    protected String getFileFromResources(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ClassPathResource(fileName).getInputStream()));
        return reader.lines().collect(Collectors.joining("\n"));
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();
        context.setMessageHistory(true);
        context.addComponent("sql", new MockComponent());
        return context;
    }
}
