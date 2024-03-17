package pl.adamsiedlecki.obm.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record BroadcastDto(int rssi,
                           String text,
                           LocalDateTime dateTime,
                           MessageTypeEnumDto messageTypeEnum,
                           Map<String,Object> textParsed) {}
