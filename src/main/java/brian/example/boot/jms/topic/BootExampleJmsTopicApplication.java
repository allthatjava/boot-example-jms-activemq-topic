package brian.example.boot.jms.topic;

import brian.example.boot.jms.topic.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.jms.Message;
import javax.jms.Session;
import java.time.LocalDateTime;

@EnableJms
@EnableScheduling
@SpringBootApplication
public class BootExampleJmsTopicApplication {

    @Autowired
    JmsTemplate jmsTemplate;

    private static int test1Counter;
    private static int test2Counter;

    public static void main(String[] args) {
        SpringApplication.run(BootExampleJmsTopicApplication.class, args);
    }

    // Client No.1 to send a message to ActiveMQ every 3 sec
//    @Scheduled(fixedRate = 3000)
//    public void sendMessage1() {
//        ChatMessage chatMessage = new ChatMessage("Test One", "Msg Test("+(++test1Counter)+")");
//        jmsTemplate.convertAndSend("topic-test", chatMessage);
//    }
//
//    // Client No.2 to send a message to ActiveMQ every 5 sec
//    @Scheduled(fixedRate = 5000)
//    public void sendMessage2() {
//        ChatMessage chatMessage = new ChatMessage("Test Two", "Two's Msg ("+(++test2Counter)+")");
//        jmsTemplate.convertAndSend("topic-test", chatMessage);
//    }

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

    @JmsListener(destination = "topic-test",
            containerFactory = "connectionFactory", // To make it durable through offline message
            subscription = "topic-subscriber2")
    public void listenTopicMessage2(@Payload ChatMessage chatMessage,
                                   @Headers MessageHeaders headers,
                                   Message message,
                                   Session session){
        LocalDateTime time = chatMessage.getTime();
        System.out.println(
                "Sub 2 Received:"+chatMessage.getName()+": "+chatMessage.getMessage()
                        +" ("+time.getHour()+":"+time.getMinute()+":"+time.getSecond()+")" );
    }

}
