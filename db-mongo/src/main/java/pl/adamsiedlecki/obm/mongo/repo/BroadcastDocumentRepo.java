package pl.adamsiedlecki.obm.mongo.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.adamsiedlecki.obm.mongo.document.BroadcastDocument;

public interface BroadcastDocumentRepo extends MongoRepository<BroadcastDocument, Long> {
}
