package com.pd.routes;

import com.datasonnet.document.MediaTypes;
import com.pd.BaseRestRouteBuilder;
import com.pd.exception.ResourceNotFoundException;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class JournalEntryRoutesImplementation extends BaseRestRouteBuilder {

    @Override
    public void configure() throws Exception {
        super.configure();

        from(direct("get-users-userId-journal-entries"))
                .routeId("get-users-journal-entries")
                .to(direct("user-exists"))
                .to(direct("set-entry-query"))
                .to(sql("classpath:/sql/journal-entries/get-journal-entries.sql"))
                .transform(datasonnetEx("resource:classpath:format-journal-entries.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA) // take in a media type java
                        .outputMediaType(MediaTypes.APPLICATION_JSON) // produce json type output
                )
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
        ;
        from(direct("post-users-userId-journal-entries"))
                .routeId("post-users-journal-entries")
                .transform(datasonnet("resource:classpath:patch-entry.ds"))
                .to(direct("set-entry-property"))
                .to(direct("user-exists"))
                .setHeader("entryId", constant(0))
                .setHeader("CamelSqlRetrieveGeneratedKeys", constant(true))
                .to(sql("classpath:/sql/journal-entries/add-entry.sql"))
                .transform(datasonnetEx("resource:classpath:get-insert-key.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
                .setHeader("entryId", simple("${body}"))
                .setHeader("Location",datasonnet("cml.header('CamelHttpUri') + '/' + cml.header('entryId')", String.class))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
                .setBody(constant(null))
        ;
        from(direct("get-users-userId-journal-entries-journalEntryId"))
                .routeId("get-users-journal-entries-inner")
                .to(direct("user-exists"), direct("entry-exists"))
                .to(sql("classpath:/sql/journal-entries/get-one-entry.sql"))
                .transform(datasonnetEx("resource:classpath:format-journal-entry.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON)
                )
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
        ;
        from(direct("delete-users-userId-journal-entries-journalEntryId"))
                .routeId("delete-users-journal-entries-inner")
                .to(direct("user-exists"), direct("entry-exists"))
                .to(sql("classpath:/sql/journal-entries/delete-entry.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
        ;

        from(direct("patch-users-userId-journal-entries-journalEntryId"))
                .routeId("patch-users-journal-entries-inner")
                .transform(datasonnet("resource:classpath:patch-entry.ds"))
                .to(direct("set-entry-property"))
                .to(direct("user-exists"), direct("entry-exists"))
                .to(sql("classpath:/sql/journal-entries/patch-entry.sql"))
                .setHeader("Location",datasonnet("cml.header('CamelHttpUri')", String.class))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .setBody(constant(null))
        ;

        from(direct("entry-exists"))
                .routeId("entry-exists")
                .to(sql("classpath:/sql/journal-entries/entry-exists.sql"))
                .transform(datasonnetEx("resource:classpath:resource-exists.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON)
                )
                .choice()
                    .when(simple("${body}"))
                    .throwException(new ResourceNotFoundException("Journal Entry doesn't exist"))
                .endChoice()
        ;

        from(direct("set-entry-property"))
                .setProperty("date", datasonnet("payload.date", String.class))
                .setProperty("summary", datasonnet("payload.summary", String.class))
                .setProperty("moodScore", datasonnet("payload.moodScore", String.class))
                .setProperty("positives", datasonnet("payload.positives", String.class))
        ;

        from(direct("set-entry-query"))
                .setProperty("date", datasonnet("cml.header('date')", String.class))
                .setProperty("moodScore", datasonnet("cml.header('moodScore')", String.class))
                .setProperty("fromMoodScore", datasonnet("cml.header('fromMoodScore')", String.class))
                .setProperty("toMoodScore", datasonnet("cml.header('toMoodScore')", String.class))
        ;
    }
}
