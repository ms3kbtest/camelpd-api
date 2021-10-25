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

public class AppointmentRoutesImplementationUnitTest extends AbstractImplementationTests{

    private static List<Map<String, Object>> appointments = new ArrayList<>();
    private static Map<String, Object> appointmentObject = new HashMap<>();


    @BeforeAll
    public static void setup() {
        appointmentObject.put("appointment_id", 2);
        appointmentObject.put("user_id", 3);
        appointmentObject.put("date", "2020-01-02");
        appointmentObject.put("start_time", "2018-03-20T13:00:00Z");
        appointmentObject.put("end_time", "2018-03-20T14:00:00Z");
        appointmentObject.put("description", "Sometext");
        appointmentObject.put("appointment_notes", "Somenotes");
        appointments.add(appointmentObject);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new AppointmentRoutesImplementation();
    }

    @Test
    public void getAppointments() throws Exception{
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        AdviceWith.adviceWith(context, "get-users-appointments",
                r -> {
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(appointments));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedBodiesReceived(getFileFromResources("data/mock-appointments.json")
                .replaceAll("[\r\n ]", "")); // removes new lines
        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 200);

        template.sendBodyAndHeader("direct:get-users-userId-appointments", null, "userId", 3);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void getOneAppointment() throws Exception{
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        AdviceWith.adviceWith(context, "get-users-appointment-inner",
                r -> {
                    r.weaveAddFirst().setHeader("appointmentId", constant(2));
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(appointments));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedBodiesReceived(getFileFromResources("data/mock-appointment.json")
                .replaceAll("[\r\n ]", "")); // removes new lines
        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 200);

        template.sendBodyAndHeader("direct:get-users-userId-appointments-appointmentId", null, "userId", 3);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void createAppointment() throws Exception{
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        List<Map<String, Object>> testList = new ArrayList<>();
        Map<String, Object> userObject = new HashMap<>();
        userObject.put("GENERATED_KEY", 7);
        testList.add(userObject);

        AdviceWith.adviceWith(context, "post-users-appointments",
                r -> {
                    // intercept checks for email and username collision
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace()
                            .setBody(constant(1))
                            .setHeader("CamelSqlGeneratedKeyRows", constant(testList))
                            .setHeader("CamelHttpUri", constant("/api/users/2/appointments"))
                    ;
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 201);
        mockResult.expectedHeaderReceived("Location", "/api/users/2/appointments/7");

        template.sendBody("direct:post-users-userId-appointments",getFileFromResources("data/user.json")
                .replaceAll("[\r\n ]", ""));

        mockResult.assertIsSatisfied();
    }

    @Test
    public void patchAppointment() throws Exception{
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        AdviceWith.adviceWith(context, "patch-users-appointments-inner",
                r -> {
                    r.weaveAddFirst()
                            .setBody(constant(getFileFromResources("data/appointment.json")))
                    ;
                    // intercept checks for email and username collision
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace()
                            .setBody(constant(null))
                            .setHeader("CamelHttpUri", constant("/api/users/2/appointments/7"))
                    ;
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 200);
        mockResult.expectedHeaderReceived("Location", "/api/users/2/appointments/7");

        template.sendBodyAndHeader("direct:patch-users-userId-appointments-appointmentId", null
                , "userId", 2);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void deleteAppointment() throws Exception{
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        AdviceWith.adviceWith(context, "delete-users-appointments-inner",
                r -> {
                    // intercept checks for email and username collision
                    r.weaveByToString(".*direct.*").replace().setBody(constant(null));
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(null));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 204);

        template.sendBodyAndHeader("direct:delete-users-userId-appointments-appointmentId",null, "userId", 1);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void appointmentExists() throws Exception {
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        List<Map<String, Object>> testMap = new ArrayList<>();
        Map<String, Object> testObject = new HashMap<>();
        testObject.put("Appointment", "Placeholder");
        testMap.add(testObject);

        AdviceWith.adviceWith(context, "appointment-exists",
                r -> {
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(testMap));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedBodiesReceived(false);

        template.sendBody("direct:appointment-exists",null);

        mockResult.assertIsSatisfied();
    }

    @Test
    public void appointmentNotExist() throws Exception {
        MockEndpoint mockResult = context.getEndpoint("mock:result", MockEndpoint.class);

        List<Map<String, Object>> testMap = new ArrayList<>();

        AdviceWith.adviceWith(context, "appointment-exists",
                r -> {
                    // intercept sql endpoint and set its body to mock sql call.
                    r.weaveByToString(".*sql:.*").replace().setBody(constant(testMap));
                    r.weaveAddLast().to("mock:result");
                }
        );

        mockResult.expectedMessageCount(0);

        template.sendBody("direct:appointment-exists",null);

        mockResult.assertIsSatisfied();
    }
}
