package brian.example.boot.jms.topic.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * The Message Object that will be sent to MQ.
 * If a customer Java object is used, it must be Serialized.
 */
public class ChatMessage implements Serializable {

    private String name;
    private String message;
    private LocalDateTime time;

    public ChatMessage(){}

    public ChatMessage(String name, String message){
        this.name =name;
        this.message = message;
        this.time = LocalDateTime.now();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
