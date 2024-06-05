# Spring Boot example with ActiveMQ Topic 
This is Spring boot application with ActiveMQ Topic. In this example, two of Scheduled message
will be sent to ActiveMQ Topic continuously and, at the same time, Listener will pull the 
messages from Topic and print them on the console.

### Apache ActiveMQ - Prerequisite
Used Apache ActiveMQ docker image.
```java
docker pull rmohr/activemq
docker run -p 61616:61616 -p 8161:8161 rmohr/activemq
```

##### application.properties
These are the minimum configuration for the Listener side. 
```properties
# YOUR ActiveMQ server IP and port. In my case, it is docker image IP/Port
spring.activemq.broker-url=tcp://{Apache ActiveMQ Server IP}:{port}
# The following lines tell ActiveMQ to trust these model as Message
spring.activemq.packages.trusted=brian.example.boot.jms.topic.model
# Following property let the application use Topic based MQ
spring.jms.pub-sub-domain=true
```

##### BootExampleJmsTopicAppliction.java
Every 3 seconds (and 5 seconds on the other one) it will send a message to 
ActiveMQ Topic `topic-test`.
```java
    @Scheduled(fixedRate = 3000)
    public void sendMessage1() {
        ChatMessage chatMessage = new ChatMessage("Test One", "Msg Test("+(++test1Counter)+")");
        jmsTemplate.convertAndSend("topic-test", chatMessage);
    }
```

### Optional 
Optional configuration to use durable subscribers
```java
    @Bean
    public JmsListenerContainerFactory<?> connectionFactory(CachingConnectionFactory connectionFactory,
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
```

When a message arrived to ActiveMQ Topic `topic-test`, this listener will pull the message
and process it. There are two listener methods in example to show multiple subscribers.
```java
    // Listens the Topic and get the message from it when new message comes in
    @JmsListener(destination = "topic-test",
                    containerFactory = "connectionFactory", // To make it durable through offline message
                    subscription = "topic-subscriber")
    public void listenTopicMessage(@Payload ChatMessage chatMessage,
                                   @Headers MessageHeaders headers,
                                   Message message,
                                   Session session){
        LocalDateTime time = chatMessage.getTime();
        System.out.println(
                "Sub 1 Received:"+chatMessage.getName()+": "+chatMessage.getMessage()
                        +" ("+time.getHour()+":"+time.getMinute()+":"+time.getSecond()+")" );
    }
```


### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.4.RELEASE/maven-plugin/)
* [Spring for Apache ActiveMQ 5](https://docs.spring.io/spring-boot/docs/2.2.4.RELEASE/reference/htmlsingle/#boot-features-activemq)

### Guides
The following guides illustrate how to use some features concretely:

* [Java Message Service API via Apache ActiveMQ Classic.](https://spring.io/guides/gs/messaging-jms/)

