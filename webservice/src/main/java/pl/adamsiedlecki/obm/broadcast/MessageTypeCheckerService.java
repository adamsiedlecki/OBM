package pl.adamsiedlecki.obm.broadcast;

import org.openapitools.model.BroadcastInfoInput;
import org.springframework.stereotype.Service;
import pl.adamsiedlecki.obm.dto.MessageTypeEnumDto;

@Service
public class MessageTypeCheckerService {

    public MessageTypeEnumDto check(BroadcastInfoInput broadcastInfoInput) {
        String text = broadcastInfoInput.getText();

        // TODO checking basing on text
        return MessageTypeEnumDto.UNKNOWN;
    }
}
