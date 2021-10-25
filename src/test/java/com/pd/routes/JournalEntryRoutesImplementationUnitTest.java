package com.pd.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.camel.builder.Builder.constant;

public class JournalEntryRoutesImplementationUnitTest extends AbstractImplementationTests{

    private static List<Map<String, Object>> entries = new ArrayList<>();
    private static Map<String, Object> entryObject = new HashMap<>();

    @BeforeAll
    public static void setup() {
        entryObject.put("entry_id", 1);
        entryObject.put("summary", "Summary");
        entryObject.put("positives", "Positives");
        entryObject.put("mood_score", 95);
        entryObject.put("date", "2021-04-02");
        entryObject.put("user_id", 1);
        entries.add(entryObject);
        entries.add(entryObject);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new JournalEntryRoutesImplementation();
    }

    @Test
    public void getEntries() throws Exception{
        MockEndpoint mockResult = getMockEndpoint("mock:result");

        AdviceWith.adviceWith(context, "get-users-journal-entries",
                r -> {
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(entries));
                    r.weaveAddLast().to("mock:result");
                }
        );
        mockResult.expectedBodiesReceived(getFileFromResources("data/mock-journal-entries.json")
                .replaceAll("[\r\n ]", "")); // removes new lines
        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 200);

        template.sendBody("direct:get-users-userId-journal-entries", null);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void getOneJournalEntry() throws Exception {
        MockEndpoint mockResult = getMockEndpoint("mock:result");

        AdviceWith.adviceWith(context, "get-users-journal-entries-inner",
                r -> {
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(entries));
                    r.weaveAddLast().to("mock:result");
                }
        );
        mockResult.expectedBodiesReceived(getFileFromResources("data/mock-journal-entry.json")
                .replaceAll("[\r\n ]", "")); // removes new lines
        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 200);

        template.sendBody("direct:get-users-userId-journal-entries-journalEntryId", null);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void createEntry() throws Exception{
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        List<Map<String, Object>> testList = new ArrayList<>();
        Map<String, Object> userObject = new HashMap<>();
        userObject.put("GENERATED_KEY", 4);
        testList.add(userObject);

        AdviceWith.adviceWith(context, "post-users-journal-entries",
                r -> {
                    // intercept checks for email and username collision
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace()
                            .setBody(constant(1))
                            .setHeader("CamelSqlGeneratedKeyRows", constant(testList))
                            .setHeader("CamelHttpUri", constant("/api/users/2/journal-entries"))
                    ;
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 201);
        mockResult.expectedHeaderReceived("Location", "/api/users/2/journal-entries/4");

        template.sendBody("direct:post-users-userId-journal-entries",getFileFromResources("data/journal-entry.json")
                .replaceAll("[\r\n ]", ""));

        mockResult.assertIsSatisfied();
    }

    @Test
    public void patchJournalEntry() throws Exception {
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        AdviceWith.adviceWith(context, "patch-users-journal-entries-inner",
                r -> {
                    r.weaveAddFirst()
                            .setBody(constant(getFileFromResources("data/journal-entry.json")))
                    ;
                    // intercept checks for email and username collision
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace()
                            .setBody(constant(null))
                            .setHeader("CamelHttpUri", constant("/api/users/2/journal-entries/9"))
                    ;
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 200);
        mockResult.expectedHeaderReceived("Location", "/api/users/2/journal-entries/9");

        template.sendBodyAndHeader("direct:patch-users-userId-journal-entries-journalEntryId", null
                , "userId", 2);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void deleteJournalEntry() throws Exception {
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        AdviceWith.adviceWith(context, "delete-users-journal-entries-inner",
                r -> {
                    // intercept checks for email and username collision
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(null));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 204);

        template.sendBodyAndHeader("direct:delete-users-userId-journal-entries-journalEntryId",null, "userId", 1);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void entryExists() throws Exception {
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        List<Map<String, Object>> testMap = new ArrayList<>();
        Map<String, Object> testObject = new HashMap<>();
        testObject.put("Journal Entry", "Placeholder");
        testMap.add(testObject);

        AdviceWith.adviceWith(context, "entry-exists",
                r -> {
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(testMap));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedBodiesReceived(false);

        template.sendBody("direct:entry-exists",null);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void entryNotExist() throws Exception {
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        List<Map<String, Object>> testMap = new ArrayList<>();

        AdviceWith.adviceWith(context, "entry-exists",
                r -> {
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(testMap));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedMessageCount(0);

        template.sendBody("direct:entry-exists",null);

        mockResult.assertIsSatisfied();
    }
}