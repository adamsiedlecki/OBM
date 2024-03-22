package pl.adamsiedlecki.obm.facade;

import pl.adamsiedlecki.obm.dto.BroadcastDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BroadcastDbFacade {

    void save(BroadcastDto broadcastDto);
    void deleteAll();
    List<BroadcastDto> findAll();
    long count();

    List<BroadcastDto> findByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime start, LocalDateTime end);
}
