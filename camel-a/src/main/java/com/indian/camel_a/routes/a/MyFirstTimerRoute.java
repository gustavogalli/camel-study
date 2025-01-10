package com.indian.camel_a.routes.a;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//@Component
public class MyFirstTimerRoute extends RouteBuilder {

    @Autowired
    private GetCurrentTimeBean getCurrentTimeBean;

    @Autowired
    private SimpleLoggingProcessingComponent loggingComponent;

    @Override
    public void configure() throws Exception {

        from("timer:first-timer")
                .log("${body}")
//                .transform().constant("My constant message")
//                .transform().constant("Time now is" + LocalDateTime.now())
                .bean(getCurrentTimeBean, "getCurrentTime")
                .log("${body}") // Time is now ...
                .bean(loggingComponent)
                .log("${body}")
                .process(new SimpleLoggingProcessor())
                .to("log:first-timer");
    }
}

@Component
class GetCurrentTimeBean {
    public String getCurrentTime(){
        return "Time is now " + LocalDateTime.now();
    }
}

@Component
class SimpleLoggingProcessingComponent {
    private Logger logger = LoggerFactory.getLogger(SimpleLoggingProcessingComponent.class);

    public void process(String message){
        logger.info("SimpleLoggingProcessingComponent {}", message);
    }
}

class SimpleLoggingProcessor implements Processor {
    private Logger logger = LoggerFactory.getLogger(SimpleLoggingProcessingComponent.class);

    public void process(Exchange exchange) throws Exception{
        logger.info("SimpleLoggingProcessor {}", exchange.getMessage().getBody());
    }
}