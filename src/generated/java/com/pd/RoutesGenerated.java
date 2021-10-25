package com.pd;

import javax.annotation.Generated;

import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import com.ms3_inc.tavros.extensions.rest.OpenApi4jValidator;

/**
 * Generated routes are based on the OpenAPI document in src/generated/api folder.
 *
 * @author Maven Archetype (camel-oas-archetype)
 */
@Generated("com.ms3_inc.camel.archetype.oas")
@Component
public class RoutesGenerated extends BaseRestRouteBuilder {
    private final String contextPath;

    public RoutesGenerated(@Value("${camel.rest.context-path}") String contextPath) {
        super();
        this.contextPath = contextPath;
    }

    /**
     * Defines Apache Camel routes using the OpenAPI REST DSL.
     * Routes are built using a get(PATH) rest message processor.
     *
     * Make changes to this file with caution.
     * If the API specification changes and this file is regenerated,
     * previous changes may be overwritten.
     */
    @Override
    public void configure() throws Exception {
        super.configure();

        restConfiguration().component("undertow");

        interceptFrom()
            .process(new OpenApi4jValidator("pd-sys-api.yaml", contextPath));

        rest()
            .get("/users")
                .id("get-users")
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .to(direct("get-users").getUri())
            .post("/users")
                .id("post-users")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .to(direct("post-users").getUri())
            .get("/users/{userId}")
                .id("get-users-userId")
                .produces("application/json")
                .to(direct("get-users-userId").getUri())
            .delete("/users/{userId}")
                .id("delete-users-userId")
                .to(direct("delete-users-userId").getUri())
            .patch("/users/{userId}")
                .id("patch-users-userId")
                .consumes("application/json")
                .to(direct("patch-users-userId").getUri())
            .get("/users/{userId}/appointments")
                .id("get-users-userId-appointments")
                .produces("application/json")
                .to(direct("get-users-userId-appointments").getUri())
            .post("/users/{userId}/appointments")
                .id("post-users-userId-appointments")
                .consumes("application/json")
                .to(direct("post-users-userId-appointments").getUri())
            .get("/users/{userId}/appointments/{appointmentId}")
                .id("get-users-userId-appointments-appointmentId")
                .produces("application/json")
                .to(direct("get-users-userId-appointments-appointmentId").getUri())
            .delete("/users/{userId}/appointments/{appointmentId}")
                .id("delete-users-userId-appointments-appointmentId")
                .to(direct("delete-users-userId-appointments-appointmentId").getUri())
            .patch("/users/{userId}/appointments/{appointmentId}")
                .id("patch-users-userId-appointments-appointmentId")
                .consumes("application/json")
                .to(direct("patch-users-userId-appointments-appointmentId").getUri())
            .get("/users/{userId}/tasks")
                .id("get-users-userId-tasks")
                .produces("application/json")
                .to(direct("get-users-userId-tasks").getUri())
            .post("/users/{userId}/tasks")
                .id("post-users-userId-tasks")
                .consumes("application/json")
                .to(direct("post-users-userId-tasks").getUri())
            .delete("/users/{userId}/tasks")
                .id("delete-users-userId-tasks")
                .to(direct("delete-users-userId-tasks").getUri())
            .get("/users/{userId}/tasks/{taskId}")
                .id("get-users-userId-tasks-taskId")
                .produces("application/json")
                .to(direct("get-users-userId-tasks-taskId").getUri())
            .delete("/users/{userId}/tasks/{taskId}")
                .id("delete-users-userId-tasks-taskId")
                .to(direct("delete-users-userId-tasks-taskId").getUri())
            .patch("/users/{userId}/tasks/{taskId}")
                .id("patch-users-userId-tasks-taskId")
                .consumes("application/json")
                .to(direct("patch-users-userId-tasks-taskId").getUri())
            .get("/users/{userId}/journal-entries")
                .id("get-users-userId-journal-entries")
                .produces("application/json")
                .to(direct("get-users-userId-journal-entries").getUri())
            .post("/users/{userId}/journal-entries")
                .id("post-users-userId-journal-entries")
                .consumes("application/json")
                .to(direct("post-users-userId-journal-entries").getUri())
            .get("/users/{userId}/journal-entries/{journalEntryId}")
                .id("get-users-userId-journal-entries-journalEntryId")
                .produces("application/json")
                .to(direct("get-users-userId-journal-entries-journalEntryId").getUri())
            .delete("/users/{userId}/journal-entries/{journalEntryId}")
                .id("delete-users-userId-journal-entries-journalEntryId")
                .to(direct("delete-users-userId-journal-entries-journalEntryId").getUri())
            .patch("/users/{userId}/journal-entries/{journalEntryId}")
                .id("patch-users-userId-journal-entries-journalEntryId")
                .consumes("application/json")
                .to(direct("patch-users-userId-journal-entries-journalEntryId").getUri())
        ;
    }
}
