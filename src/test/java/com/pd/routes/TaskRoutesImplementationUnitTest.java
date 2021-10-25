package com.pd.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.camel.builder.Builder.constant;

public class TaskRoutesImplementationUnitTest extends AbstractImplementationTests {

    private static List<Map<String, Object>> tasks = new ArrayList<>();
    private static Map<String, Object> taskObject = new HashMap<>();


    @BeforeAll
    public static void setup() {
        taskObject.put("task_id", 2);
        taskObject.put("user_id", 3);
        taskObject.put("done", false);
        taskObject.put("name", "ProjectOAS");
        taskObject.put("deadline", "2018-03-20T13:00:00Z");
        taskObject.put("progress", 50);
        taskObject.put("priority", 10);
        tasks.add(taskObject);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new TaskRoutesImplementation();
    }

    @Test
    public void getTasks() throws Exception {
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        AdviceWith.adviceWith(context, "get-users-tasks",
                r -> {
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(tasks));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedBodiesReceived(getFileFromResources("data/mock-tasks.json")
                .replaceAll("[\r\n ]", "")); // removes new lines
        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 200);

        template.sendBodyAndHeader("direct:get-users-userId-tasks", null, "userId", 3);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void getOneTask() throws Exception {
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        AdviceWith.adviceWith(context, "get-users-tasks-inner",
                r -> {
                    r.weaveAddFirst().setHeader("taskId", constant(2));
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(tasks));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedBodiesReceived(getFileFromResources("data/mock-task.json")
                .replaceAll("[\r\n ]", "")); // removes new lines
        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 200);

        template.sendBodyAndHeader("direct:get-users-userId-tasks-taskId", null, "userId", 3);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void createTask() throws Exception{
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        List<Map<String, Object>> testList = new ArrayList<>();
        Map<String, Object> userObject = new HashMap<>();
        userObject.put("GENERATED_KEY", 5);
        testList.add(userObject);

        AdviceWith.adviceWith(context, "post-users-tasks",
                r -> {
                    // intercept checks for email and username collision
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace()
                            .setBody(constant(1))
                            .setHeader("CamelSqlGeneratedKeyRows", constant(testList))
                            .setHeader("CamelHttpUri", constant("/api/users/2/tasks"))
                    ;
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 201);
        mockResult.expectedHeaderReceived("Location", "/api/users/2/tasks/5");

        template.sendBody("direct:post-users-userId-tasks",getFileFromResources("data/task.json")
                .replaceAll("[\r\n ]", ""));

        mockResult.assertIsSatisfied();
    }

    @Test
    public void patchTask() throws Exception{
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        AdviceWith.adviceWith(context, "patch-users-tasks-inner",
                r -> {
                    r.weaveAddFirst()
                            .setBody(constant(getFileFromResources("data/task.json")))
                    ;
                    // intercept checks for email and username collision
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace()
                            .setBody(constant(null))
                            .setHeader("CamelHttpUri", constant("/api/users/2/tasks/9"))
                    ;
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 200);
        mockResult.expectedHeaderReceived("Location", "/api/users/2/tasks/9");

        template.sendBodyAndHeader("direct:patch-users-userId-tasks-taskId", null
                , "userId", 2);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void deleteTasks() throws Exception{
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        AdviceWith.adviceWith(context, "delete-users-tasks",
                r -> {
                    // intercept checks for email and username collision
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(null));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 204);

        template.sendBodyAndHeader("direct:delete-users-userId-tasks",null, "userId", 1);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void deleteOneTask() throws Exception{
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        AdviceWith.adviceWith(context, "delete-users-tasks-inner",
                r -> {
                    // intercept checks for email and username collision
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(null));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 204);

        template.sendBodyAndHeader("direct:delete-users-userId-tasks-taskId",null, "userId", 1);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void taskExists() throws Exception {
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        List<Map<String, Object>> testMap = new ArrayList<>();
        Map<String, Object> testObject = new HashMap<>();
        testObject.put("Task", "Placeholder");
        testMap.add(testObject);

        AdviceWith.adviceWith(context, "task-exists",
                r -> {
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(testMap));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedBodiesReceived(false);

        template.sendBody("direct:task-exists",null);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void taskNotExist() throws Exception {
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        List<Map<String, Object>> testMap = new ArrayList<>();

        AdviceWith.adviceWith(context, "task-exists",
                r -> {
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(testMap));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedMessageCount(0);

        template.sendBody("direct:task-exists",null);

        mockResult.assertIsSatisfied();
    }
}
