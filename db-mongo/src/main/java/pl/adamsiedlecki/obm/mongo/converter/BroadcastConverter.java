package pl.adamsiedlecki.obm.mongo.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.adamsiedlecki.obm.dto.BroadcastDto;
import pl.adamsiedlecki.obm.mongo.document.BroadcastDocument;

@RequiredArgsConstructor
@Service
public class BroadcastConverter {

    private final ObjectMapper objectMapper;

    public BroadcastDocument convert(BroadcastDto broadcastDto) {
        return objectMapper.convertValue(broadcastDto, BroadcastDocument.class);
    }

    public BroadcastDto convert(BroadcastDocument broadcastDocument) {
        return objectMapper.convertValue(broadcastDocument, BroadcastDto.class);
    }
}
