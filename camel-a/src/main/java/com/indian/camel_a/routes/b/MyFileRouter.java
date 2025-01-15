package com.indian.camel_a.routes.b;

import org.apache.camel.Body;
import org.apache.camel.ExchangeProperties;
import org.apache.camel.Header;
import org.apache.camel.Headers;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MyFileRouter extends RouteBuilder {

    @Autowired
    private DeciderBean deciderBean;

    @Override
    public void configure() throws Exception {
        from("file:files/input")
                .routeId("Files-Input-Route")
                .transform().body(String.class)
                .choice()
                    .when(simple("${file:ext} ends with 'xml'"))
                        .log("XML file")
//                    .when(simple("${body} contains 'frommm'"))
                .when(method(deciderBean))
                        .log("Not an XML file, but contains 'frommm'")
                    .otherwise()
                        .log("Not an XML file")
                .end()
//                .to("direct://log-file-values")
                .to("file:files/output");

        from("direct:log-file-values")
                .log("${messageHistory}")
                .log("${file:name}")
                .log("${file:size}")
                .log("${routeId}");

    }
}

@Component
class DeciderBean {

    Logger logger = LoggerFactory.getLogger(DeciderBean.class);

    public boolean isThisConditionMet(
            @Body String body,
            @Headers Map<String, String> headers,
            @Header("CamelFileAbsolutePath") String path,
            @ExchangeProperties Map<String, String> exchangeProperties
    ) {
        logger.info("\n=============== Decider Bean =============== \n{} \n===== HEADERS ===== \n{} \n===== PATH =====\n{} \n===== EXCHANGE PROPERTIES =====\n{}", body, headers, path, exchangeProperties);
        return true;
    }
}
