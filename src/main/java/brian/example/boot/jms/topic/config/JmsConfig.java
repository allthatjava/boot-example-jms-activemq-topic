package brian.example.boot.jms.topic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;


/**
 * NOTE: These two configuration are required because I used LocalDateTime in ChatMessage.
 * If I used only simple variable types, I wouldn't have needed these configuration.
 */
@Configuration
public class JmsConfig {

    @Bean
    public JmsListenerContainerFactory<DefaultMessageListenerContainer>
                        connectionFactory(CachingConnectionFactory connectionFactory,
                                          DefaultJmsListenerContainerFactoryConfigurer configurer){
        connectionFactory.setClientId("Unique-Client-ID-1");
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory,connectionFactory);
        factory.setConcurrency("1-10");
        factory.setPubSubDomain(true);          // Set it as Publisher/Subscriber model instead of Queue
        factory.setSubscriptionShared(true);    // Allow to multiple consumer to process messages
        factory.setSubscriptionDurable(true);   // Allow to pick up messages added while consumer was offline
        return factory;
    }

    @Bean
    public MessageConverter messageConverter(){
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setObjectMapper( objectMapper() );
        return converter;
    }

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
