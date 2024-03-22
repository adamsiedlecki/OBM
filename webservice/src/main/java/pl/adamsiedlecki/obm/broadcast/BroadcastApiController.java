package pl.adamsiedlecki.obm.broadcast;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.api.BroadcastsApi;
import org.openapitools.model.BroadcastInfoInput;
import org.openapitools.model.BroadcastListOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@Slf4j
@RequiredArgsConstructor
public class BroadcastApiController implements BroadcastsApi {

    private final BroadcastProcessorService broadcastService;

    @Override
    public ResponseEntity<Void> processBroadcastInformation(BroadcastInfoInput broadcastInfoInput) {
        log.info("Broadcast information received: {}", broadcastInfoInput);
        long start = System.currentTimeMillis();
        if (StringUtils.isBlank(broadcastInfoInput.getText())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        broadcastService.process(broadcastInfoInput);
        long end = System.currentTimeMillis();
        log.info("Processing took: {} millis", end-start);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BroadcastListOutput> getBroadcastInformation() {
        //TODO
        return new ResponseEntity<>(new BroadcastListOutput(), HttpStatus.OK);
    }
}
