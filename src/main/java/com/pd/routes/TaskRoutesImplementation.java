package com.pd.routes;

import com.datasonnet.document.MediaTypes;
import com.pd.BaseRestRouteBuilder;
import com.pd.exception.ResourceNotFoundException;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class TaskRoutesImplementation extends BaseRestRouteBuilder {
    @Override
    public void configure() throws Exception {
        super.configure();

        from(direct("get-users-userId-tasks"))
                .routeId("get-users-tasks")
                .to(direct("user-exists"))
                .to(direct("set-task-query"))
                .to(sql("classpath:/sql/tasks/get-tasks.sql"))
                .transform(datasonnetEx("resource:classpath:format-tasks.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA) // take in a media type java
                        .outputMediaType(MediaTypes.APPLICATION_JSON) // produce json type output
                )
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
        ;
        from(direct("post-users-userId-tasks"))
                .routeId("post-users-tasks")
                .transform(datasonnet("resource:classpath:patch-task.ds"))
                .to(direct("set-task-property"))
                .to(direct("user-exists"))
                .setHeader("taskId", constant(0))
                .setHeader("CamelSqlRetrieveGeneratedKeys", constant(true))
                .to(sql("classpath:/sql/tasks/add-task.sql"))
                .transform(datasonnetEx("resource:classpath:get-insert-key.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
                .setHeader("taskId", simple("${body}"))
                .setHeader("Location",datasonnet("cml.header('CamelHttpUri') + '/' + cml.header('taskId')", String.class))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
                .setBody(constant(null))
        ;

        from(direct("delete-users-userId-tasks"))
                .routeId("delete-users-tasks")
                .to(direct("user-exists"))
                .setProperty("done", datasonnet("cml.header('done')", Boolean.class))
                .to(sql("classpath:/sql/tasks/delete-all-tasks.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
        ;
        from(direct("get-users-userId-tasks-taskId"))
                .routeId("get-users-tasks-inner")
                .to(direct("user-exists"), direct("task-exists"))
                .to(sql("classpath:/sql/tasks/get-one-task.sql"))
                .transform(datasonnetEx("resource:classpath:format-task.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON)
                )
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
        ;
        from(direct("delete-users-userId-tasks-taskId"))
                .routeId("delete-users-tasks-inner")
                .to(direct("user-exists"), direct("task-exists"))
                .to(sql("classpath:/sql/tasks/delete-one-task.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
        ;
        from(direct("patch-users-userId-tasks-taskId"))
                .routeId("patch-users-tasks-inner")
                .transform(datasonnet("resource:classpath:patch-task.ds"))
                .to(direct("set-task-property"))
                .to(direct("user-exists"), direct("task-exists"))
                .to(sql("classpath:/sql/tasks/patch-task.sql"))
                .setHeader("Location",datasonnet("cml.header('CamelHttpUri')", String.class))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .setBody(constant(null))
        ;

        from(direct("task-exists"))
                .routeId("task-exists")
                .to(sql("classpath:/sql/tasks/task-exists.sql"))
                .transform(datasonnetEx("resource:classpath:resource-exists.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON)
                )
                .choice()
                    .when(simple("${body}"))
                    .throwException(new ResourceNotFoundException("Task doesn't exist"))
                .endChoice()
        ;

        from(direct("set-task-property"))
                .setProperty("done", datasonnet("payload.done", Boolean.class))
                .setProperty("deadline", datasonnet("payload.deadline", String.class))
                .setProperty("name", datasonnet("payload.name", String.class))
                .setProperty("priority", datasonnet("payload.priority", String.class))
                .setProperty("progress", datasonnet("payload.progress", String.class))
        ;

        from(direct("set-task-query"))
                .setProperty("done", datasonnet("cml.header('done')", Boolean.class))
                .setProperty("priority", datasonnet("cml.header('priority')", String.class))
                .setProperty("progress", datasonnet("cml.header('progress')", String.class))
                .setProperty("deadline", datasonnet("if cml.header('deadline') == null then null else " +
                        "ds.datetime.format(cml.header('deadline'), \"yyyy-MM-dd HH:mm:ss\")", String.class))
                .setProperty("priorityGTE", datasonnet("cml.header('priorityGTE')", String.class))
                .setProperty("fromProgress", datasonnet("cml.header('fromProgress')", String.class))
                .setProperty("toProgress", datasonnet("cml.header('toProgress')", String.class))
        ;
    }
}
