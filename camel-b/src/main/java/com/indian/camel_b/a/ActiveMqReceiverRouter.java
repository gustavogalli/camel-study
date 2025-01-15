package com.indian.camel_b.a;

import com.indian.camel_b.CurrencyExchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class ActiveMqReceiverRouter extends RouteBuilder {

    @Autowired
    private MyCurrencyExchangeProcessor myCurrencyExchangeProcessor;

    @Autowired
    private MyCurrencyExchangeTransformer myCurrencyExchangeTransformer;

    @Override
    public void configure() throws Exception {

        // JSON
        from("activemq:my-activemq-queue")
                .log("${body}")
                .unmarshal().json(JsonLibrary.Jackson, CurrencyExchange.class)
                .bean(myCurrencyExchangeProcessor)
                .bean(myCurrencyExchangeTransformer)
                .to("log:received-message-from-active-mq");

        // XML
        from("activemq:my-activemq-xml-queue")
                .log("${body}")
                .unmarshal()
                .jacksonxml(CurrencyExchange.class)
                .to("log:received-message-from-active-mq");
    }
}

@Component
class MyCurrencyExchangeProcessor {
    public void processMessage(CurrencyExchange currencyExchange){
        Logger logger = LoggerFactory.getLogger(ActiveMqReceiverRouter.class);
        logger.info("Do some processing with {}", currencyExchange.getConversionMultiple());
    }
}

@Component
class MyCurrencyExchangeTransformer {
    public CurrencyExchange processMessage(CurrencyExchange currencyExchange){
        currencyExchange.setConversionMultiple(currencyExchange.getConversionMultiple().multiply(BigDecimal.TEN));

        Logger logger = LoggerFactory.getLogger(ActiveMqReceiverRouter.class);
        logger.info("Do some processing with {}", currencyExchange.getConversionMultiple());

        return currencyExchange;
    }
}

