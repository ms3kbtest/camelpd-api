package com.pd.routes;

import com.datasonnet.document.MediaTypes;
import com.pd.BaseRestRouteBuilder;
import com.pd.exception.BadRequestException;
import com.pd.exception.ResourceNotFoundException;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class UserRoutesImplementation extends BaseRestRouteBuilder {
    @Override
    public void configure() throws Exception {
        super.configure();

        from(direct("get-users"))
                .routeId("get-users-inner")
                .to(sql("classpath:/sql/users/get-users.sql"))
                .transform(datasonnetEx("resource:classpath:format-users.ds", String.class)
                    .bodyMediaType(MediaTypes.APPLICATION_JAVA) // take in a media type java
                    .outputMediaType(MediaTypes.APPLICATION_JSON) // produce json type output
                )
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
        ;

        from(direct("post-users"))
                .routeId("post-users-inner")
                .to(direct("set-user-property"))
                .setHeader("userId", constant(0)) // put on header so we can use it on patch too
                .setHeader("CamelSqlRetrieveGeneratedKeys", constant(true))
                .to(direct("get-email-unique").getUri(),direct("get-username-unique").getUri()) // validate username and email doesnt already exist
                .to(sql("classpath:/sql/users/add-user.sql"))
                .transform(datasonnetEx("resource:classpath:get-insert-key.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
                .setHeader("userId", simple("${body}"))
                .to(sql("classpath:/sql/users/add-default-settings.sql"))
                .setHeader("Location",datasonnet("cml.header('CamelHttpUri') + '/' + cml.header('userId')", String.class))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
                .setBody(constant(null))
        ;

        from(direct("get-users-userId"))
                .routeId("get-user-inner")
                .to(direct("user-exists").getUri())
                .to(sql("classpath:/sql/users/get-one-user.sql"))
                .transform(datasonnetEx("resource:classpath:format-user.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON)
                )
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))

        ;
        from(direct("delete-users-userId"))
                .routeId("delete-user-inner")
                .to(direct("user-exists").getUri())
                .to(sql("classpath:/sql/users/delete-user.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
        ;

        from(direct("patch-users-userId"))
                .routeId("patch-user-inner")
                .transform(datasonnetEx("resource:classpath:patch-user.ds"))
                .to(direct("set-user-property"))
                .to(direct("set-settings-property"))
                .to(direct("get-email-unique").getUri(),direct("get-username-unique").getUri()) // check if username & email not conflict
                .to(sql("classpath:/sql/users/patch-user.sql"))
                .to(sql("classpath:/sql/users/patch-settings.sql"))
                .setHeader("Location",simple("/api/users/${header.userId}"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .setBody(constant(null))
        ;

        from(direct("get-email-unique"))
                .routeId("get-email-unique")
                .to(sql("classpath:/sql/users/get-email-unique.sql"))
                .transform(datasonnetEx("payload[0][\"count(email)\"] > 0", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON)
                )
                .choice()
                    .when(simple("${body}"))
                    .throwException(new BadRequestException("Email Taken"))
                .endChoice()
        ;

        from(direct("get-username-unique"))
                .routeId("get-username-unique")
                .to(sql("classpath:/sql/users/get-username-unique.sql"))
                .transform(datasonnetEx("payload[0][\"count(username)\"] > 0", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON)
                )
                .choice()
                    .when(simple("${body}"))
                    .throwException(new BadRequestException("Username Taken"))
                .endChoice()
        ;

        from(direct("user-exists"))
                .routeId("user-exists")
                .to(sql("classpath:/sql/users/user-exists.sql"))
                .transform(datasonnetEx("resource:classpath:resource-exists.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON)
                )
                .choice()
                    .when(simple("${body}"))
                    .throwException(new ResourceNotFoundException("User doesn't exist"))
                .endChoice()
        ;

        from(direct("set-user-property"))
                .setProperty("first", datasonnet("payload.firstName", String.class))
                .setProperty("last", datasonnet("payload.lastName", String.class))
                .setProperty("username", datasonnet("payload.username", String.class))
                .setProperty("email", datasonnet("payload.email", String.class))
                .setProperty("birthday", datasonnet("payload.birthday", String.class))
        ;

        from(direct("set-settings-property"))
                .setProperty("alerts", datasonnet("payload.appAlerts", Boolean.class))
                .setProperty("notifications", datasonnet("payload.emailNotifications", Boolean.class))
                .setProperty("theme", datasonnet("payload.theme", String.class))
        ;
    }
}