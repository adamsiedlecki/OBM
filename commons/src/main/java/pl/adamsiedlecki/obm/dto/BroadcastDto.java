package pl.adamsiedlecki.obm.dto;

import java.time.LocalDateTime;

public record BroadcastDto(int rssi, String text, LocalDateTime dateTime, MessageTypeEnumDto messageTypeEnum) {}
