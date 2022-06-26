package pl.adamsiedlecki.obm.broadcast;

import lombok.RequiredArgsConstructor;
import org.openapitools.model.BroadcastInfoInput;
import org.springframework.stereotype.Service;
import pl.adamsiedlecki.obm.dto.BroadcastDto;
import pl.adamsiedlecki.obm.dto.MessageTypeEnumDto;
import pl.adamsiedlecki.obm.facade.BroadcastDbFacade;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BroadcastProcessorService {

    private final BroadcastDbFacade broadcastDbFacade;
    private final MessageTypeCheckerService messageTypeCheckerService;

    public void process(BroadcastInfoInput broadcastInfoInput) {
        saveToDb(broadcastInfoInput);
    }

    private void saveToDb(BroadcastInfoInput input) {
        MessageTypeEnumDto messageType = messageTypeCheckerService.check(input);
        BroadcastDto broadcastDto = new BroadcastDto(input.getRssi(), input.getText(), LocalDateTime.now(), messageType);
        broadcastDbFacade.save(broadcastDto);
    }
}
