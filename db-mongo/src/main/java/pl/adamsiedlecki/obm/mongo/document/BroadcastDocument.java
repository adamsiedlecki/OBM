package pl.adamsiedlecki.obm.mongo.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(value = "broadcasts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BroadcastDocument {

    @Id
    private String id;
    private int rssi;
    private String text;
    private LocalDateTime dateTime;
    private MessageTypeEnum messageTypeEnum;
}
