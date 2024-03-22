package pl.adamsiedlecki.obm.mongo.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.adamsiedlecki.obm.mongo.document.BroadcastDocument;

import java.time.LocalDateTime;
import java.util.List;

public interface BroadcastDocumentRepo extends MongoRepository<BroadcastDocument, Long> {

    List<BroadcastDocument> findByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime start, LocalDateTime end);
}
