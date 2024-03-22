package pl.adamsiedlecki.obm.mongo.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.adamsiedlecki.obm.dto.BroadcastDto;
import pl.adamsiedlecki.obm.facade.BroadcastDbFacade;
import pl.adamsiedlecki.obm.mongo.converter.BroadcastConverter;
import pl.adamsiedlecki.obm.mongo.repo.BroadcastDocumentRepo;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BroadcastMongoFacade implements BroadcastDbFacade {

    private final BroadcastDocumentRepo broadcastDocumentRepo;
    private final BroadcastConverter broadcastConverter;

    @Override
    public void save(BroadcastDto broadcastDto) {
        broadcastDocumentRepo.save(broadcastConverter.convert(broadcastDto));
    }

    @Override
    public void deleteAll() {
        broadcastDocumentRepo.deleteAll();
    }

    @Override
    public List<BroadcastDto> findAll() {
        return broadcastDocumentRepo.findAll().stream().map(broadcastConverter::convert).toList();
    }

    @Override
    public long count() {
        return broadcastDocumentRepo.count();
    }


    @Override
    public List<BroadcastDto> findByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime start, LocalDateTime end) {
        return broadcastDocumentRepo.findByDateTimeBetweenOrderByDateTimeDesc(start, end)
                .stream()
                .map(broadcastConverter::convert)
                .toList();
    }
}
