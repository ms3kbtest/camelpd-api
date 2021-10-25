package com.pd.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.camel.builder.Builder.constant;


public class UserRoutesImplementationUnitTest extends AbstractImplementationTests {

    private static List<Map<String, Object>> users = new ArrayList<>();
    private static Map<String, Object> userObject = new HashMap<>();

    @BeforeAll
    public static void setup() {
        userObject.put("id", 2);
        userObject.put("first_name", "Johnny");
        userObject.put("last_name", "Boy");
        userObject.put("username", "fjgfhbgn");
        userObject.put("email", "jfggg");
        userObject.put("birthday", "1950-02-03");
        userObject.put("child_id", 2);
        userObject.put("user_id", 2);
        userObject.put("start_time", null);
        userObject.put("end_time", null);
        userObject.put("date", null);
        userObject.put("description", null);
        userObject.put("appointment_notes", null);
        userObject.put("done", 1);
        userObject.put("name", "name");
        userObject.put("deadline", "2019-04-0400:00:00.0");
        userObject.put("progress", 0);
        userObject.put("priority", 10);
        userObject.put("settings_id", 2);
        userObject.put("user_id", 2);
        userObject.put("app_alerts", 0);
        userObject.put("email_notifications", 0);
        userObject.put("theme", 0);
        users.add(userObject);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new UserRoutesImplementation();
    }

    @Test
    public void getUsers() throws Exception{
        MockEndpoint mockResult = getMockEndpoint("mock:result");

        AdviceWith.adviceWith(context, "get-users-inner",
                r -> {
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(users));
                    r.weaveAddLast().to("mock:result");
                }
        );
        mockResult.expectedBodiesReceived(getFileFromResources("data/mock-users.json")
                .replaceAll("[\r\n ]", "")); // removes new lines
        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 200);

        template.sendBody("direct:get-users", "");

        mockResult.assertIsSatisfied();
    }

    @Test
    public void getOneUser() throws Exception{
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        AdviceWith.adviceWith(context, "get-user-inner",
                r -> {
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(users));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedBodiesReceived(getFileFromResources("data/mock-user.json")
                .replaceAll("[\r\n ]", "")); // removes new lines
        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 200);

        template.sendBodyAndHeader("direct:get-users-userId",null, "userId", 5);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void createUser() throws Exception{
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        List<Map<String, Object>> testList = new ArrayList<>();
        Map<String, Object> userObject = new HashMap<>();
        userObject.put("GENERATED_KEY", 2);
        testList.add(userObject);

        AdviceWith.adviceWith(context, "post-users-inner",
                r -> {
                    // intercept checks for email and username collision
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace()
                            .setBody(constant(1))
                            .setHeader("CamelSqlGeneratedKeyRows", constant(testList))
                            .setHeader("CamelHttpUri", constant("/api/users"))
                    ;
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 201);
        mockResult.expectedHeaderReceived("Location", "/api/users/2");

        template.sendBody("direct:post-users",getFileFromResources("data/user.json")
                .replaceAll("[\r\n ]", ""));

        mockResult.assertIsSatisfied();
    }

    @Test
    public void deleteUser() throws Exception{
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        AdviceWith.adviceWith(context, "delete-user-inner",
                r -> {
                    // intercept checks for email and username collision
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(null));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 204);

        template.sendBodyAndHeader("direct:delete-users-userId",null, "userId", 1);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void emailExists() throws Exception {
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        List<Map<String, Object>> testList = new ArrayList<>();
        Map<String, Object> userObject = new HashMap<>();
        userObject.put("count(email)", 1);
        testList.add(userObject);

        AdviceWith.adviceWith(context, "get-email-unique",
                r -> {
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(testList));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedMessageCount(0);

        template.sendBody("direct:get-email-unique",null);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void emailNotExists() throws Exception{
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        List<Map<String, Object>> testList = new ArrayList<>();
        Map<String, Object> userObject = new HashMap<>();
        userObject.put("count(email)", 0);
        testList.add(userObject);

        AdviceWith.adviceWith(context, "get-email-unique",
                r -> {
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(testList));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedBodiesReceived(false);

        template.sendBody("direct:get-email-unique",null);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void usernameExists() throws Exception{
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        List<Map<String, Object>> testList = new ArrayList<>();
        Map<String, Object> userObject = new HashMap<>();
        userObject.put("count(username)", 2);
        testList.add(userObject);

        AdviceWith.adviceWith(context, "get-username-unique",
                r -> {
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(testList));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedMessageCount(0);

        template.sendBody("direct:get-username-unique",null);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void usernameNotExists() throws Exception{
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        List<Map<String, Object>> testList = new ArrayList<>();
        Map<String, Object> userObject = new HashMap<>();
        userObject.put("count(username)", 0);
        testList.add(userObject);

        AdviceWith.adviceWith(context, "get-username-unique",
                r -> {
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(testList));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedBodiesReceived(false);

        template.sendBody("direct:get-username-unique",null);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void userExists() throws Exception{
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        List<Map<String, Object>> testMap = new ArrayList<>();
        Map<String, Object> testObject = new HashMap<>();
        testObject.put("Placeholder", "Test");
        testMap.add(testObject);

        AdviceWith.adviceWith(context, "user-exists",
                r -> {
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(testMap));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedBodiesReceived(false);

        template.sendBody("direct:user-exists",null);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void userNotExist() throws Exception{
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        List<Map<String, Object>> testMap = new ArrayList<>();

        AdviceWith.adviceWith(context, "user-exists",
                r -> {
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(testMap));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedMessageCount(0);

        template.sendBody("direct:user-exists",null);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void patchUsers() throws Exception {
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        AdviceWith.adviceWith(context, "patch-user-inner",
                r -> {
                    r.weaveAddFirst()
                            .setBody(constant(getFileFromResources("data/user.json")))
                    ;
                    // intercept checks for email and username collision
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace()
                            .setBody(constant(null))
                    ;
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 200);
        mockResult.expectedHeaderReceived("Location", "/api/users/2");

        template.sendBodyAndHeader("direct:patch-users-userId", null
                , "userId", 2);

        mockResult.assertIsSatisfied();
    }
}
