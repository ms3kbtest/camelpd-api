package com.pd.routes;

import com.datasonnet.document.MediaTypes;
import com.pd.BaseRestRouteBuilder;
import com.pd.exception.ResourceNotFoundException;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class AppointmentRoutesImplementation extends BaseRestRouteBuilder {
    @Override
    public void configure() throws Exception {
        super.configure();

        from(direct("get-users-userId-appointments"))
                .routeId("get-users-appointments")
                .to(direct("user-exists"))
                .to(direct("set-appointment-query"))
                .to(sql("classpath:/sql/appointments/get-appointments.sql"))
                .transform(datasonnetEx("resource:classpath:format-appointments.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA) // take in a media type java
                        .outputMediaType(MediaTypes.APPLICATION_JSON) // produce json type output
                )
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
        ;

        from(direct("post-users-userId-appointments"))
                .routeId("post-users-appointments")
                .transform(datasonnetEx("resource:classpath:patch-appointment.ds"))
                .to(direct("set-appointment-property"))
                .to(direct("user-exists"))
                .setHeader("apptId", constant(0))
                .setHeader("CamelSqlRetrieveGeneratedKeys", constant(true))
                .to(sql("classpath:/sql/appointments/add-appointment.sql"))
                .transform(datasonnetEx("resource:classpath:get-insert-key.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
                .setHeader("apptId", simple("${body}"))
                .setHeader("Location",datasonnet("cml.header('CamelHttpUri') + '/' + cml.header('apptId')", String.class))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
                .setBody(constant(null))
        ;


        from(direct("get-users-userId-appointments-appointmentId"))
                .routeId("get-users-appointment-inner")
                .to(direct("user-exists"), direct("appointment-exists"))
                .to(sql("classpath:/sql/appointments/get-one-appointment.sql"))
                .transform(datasonnetEx("resource:classpath:format-appointment.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON)
                )
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
        ;
        from(direct("delete-users-userId-appointments-appointmentId"))
                .routeId("delete-users-appointments-inner")
                .to(direct("user-exists"), direct("appointment-exists"))
                .to(sql("classpath:/sql/appointments/delete-appointment.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
        ;

        from(direct("patch-users-userId-appointments-appointmentId"))
                .routeId("patch-users-appointments-inner")
                .transform(datasonnet("resource:classpath:patch-appointment.ds"))
                .to(direct("set-appointment-property"))
                .to(direct("user-exists"), direct("appointment-exists"))
                .to(sql("classpath:/sql/appointments/patch-appointment.sql"))
                .setHeader("Location",datasonnet("cml.header('CamelHttpUri')", String.class))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .setBody(constant(null))
        ;

        from(direct("appointment-exists"))
                .routeId("appointment-exists")
                .to(sql("classpath:/sql/appointments/appointment-exists.sql"))
                .transform(datasonnetEx("resource:classpath:resource-exists.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON)
                )
                .choice()
                    .when(simple("${body}"))
                    .throwException(new ResourceNotFoundException("Appointment doesn't exist"))
                .endChoice()
        ;

        from(direct("set-appointment-property"))
                .setProperty("date", datasonnet("payload.date", String.class))
                .setProperty("startTime", datasonnet("payload.startTime", String.class))
                .setProperty("endTime", datasonnet("payload.endTime", String.class))
                .setProperty("description", datasonnet("payload.description", String.class))
                .setProperty("appointmentNotes", datasonnet("payload.appointmentNotes", String.class))
        ;

        from(direct("set-appointment-query"))
                .setProperty("date", datasonnet("cml.header('date')", String.class))
                .setProperty("fromDate", datasonnet("cml.header('fromDate')", String.class))
                .setProperty("toDate", datasonnet("cml.header('toDate')", String.class))
                .setProperty("descKeywords", datasonnet("if cml.header('descKeywords') == null then null else '%'+cml.header('descKeywords')+'%'", String.class))
        ;
    }
}
