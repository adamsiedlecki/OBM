package pl.adamsiedlecki.obm.broadcast;

import lombok.RequiredArgsConstructor;
import org.openapitools.model.BroadcastInfoInput;
import org.springframework.stereotype.Service;
import pl.adamsiedlecki.obm.dto.BroadcastDto;
import pl.adamsiedlecki.obm.dto.MessageTypeEnumDto;
import pl.adamsiedlecki.obm.facade.BroadcastDbFacade;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

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
        String decodedText = new String(Base64.getDecoder().decode(input.getText()), StandardCharsets.UTF_8);
        BroadcastDto broadcastDto = new BroadcastDto(input.getRssi(), decodedText, LocalDateTime.now(), messageType);
        broadcastDbFacade.save(broadcastDto);
    }
}
