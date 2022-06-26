package pl.adamsiedlecki.obm.mongo.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.adamsiedlecki.obm.dto.BroadcastDto;
import pl.adamsiedlecki.obm.facade.BroadcastDbFacade;
import pl.adamsiedlecki.obm.mongo.converter.BroadcastConverter;
import pl.adamsiedlecki.obm.mongo.repo.BroadcastDocumentRepo;

@Service
@RequiredArgsConstructor
public class BroadcastMongoFacade implements BroadcastDbFacade {

    private final BroadcastDocumentRepo broadcastDocumentRepo;
    private final BroadcastConverter broadcastConverter;

    @Override
    public void save(BroadcastDto broadcastDto) {
        broadcastDocumentRepo.save(broadcastConverter.convert(broadcastDto));
    }
}
