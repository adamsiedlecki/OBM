package pl.adamsiedlecki.obm.broadcast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.BroadcastInfoInput;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.adamsiedlecki.obm.dto.BroadcastDto;
import pl.adamsiedlecki.obm.dto.MessageTypeEnumDto;
import pl.adamsiedlecki.obm.facade.BroadcastDbFacade;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BroadcastProcessorService {

    private final BroadcastDbFacade broadcastDbFacade;
    private final MessageTypeCheckerService messageTypeCheckerService;
    private final ObjectMapper objectMapper;

    @Async
    public void process(BroadcastInfoInput broadcastInfoInput) {
        saveToDb(broadcastInfoInput);
    }

    private void saveToDb(BroadcastInfoInput input) {
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(input.getText());
        } catch (IllegalArgumentException e) {
            log.error("Text is not base64!");
            throw e;
        }
        String decodedText = new String(decoded, StandardCharsets.UTF_8);
        MessageTypeEnumDto messageType = messageTypeCheckerService.check(decodedText);

        Map<String, Object> parsedText = null;

        try {
            parsedText = objectMapper.readValue(decoded, new TypeReference<HashMap<String,Object>>() {});
        } catch (Exception e) {
            log.info("Text cannot be parsed as json: " + decodedText);
        }

        BroadcastDto broadcastDto = new BroadcastDto(input.getRssi(), decodedText, LocalDateTime.now(), messageType, parsedText);

        broadcastDbFacade.save(broadcastDto);
    }
}
