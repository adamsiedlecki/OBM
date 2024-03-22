package pl.adamsiedlecki.obm.broadcast;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.api.BroadcastsApi;
import org.openapitools.model.Broadcast;
import org.openapitools.model.BroadcastInfoInput;
import org.openapitools.model.BroadcastListOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.adamsiedlecki.obm.dto.BroadcastDto;
import pl.adamsiedlecki.obm.facade.BroadcastDbFacade;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1")
@Slf4j
@RequiredArgsConstructor
public class BroadcastApiController implements BroadcastsApi {

    private final BroadcastProcessorService broadcastService;
    private final BroadcastDbFacade broadcastDbFacade;

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
        LocalDateTime now = LocalDateTime.now();
        List<BroadcastDto> broadcastsList = broadcastDbFacade.findByDateTimeBetweenOrderByDateTimeDesc(now.minusHours(1), now);
        List<Broadcast> broadcastsApiModelList = broadcastsList.stream()
                .map(b -> new Broadcast().dateTime(b.dateTime()).text(b.text()))
                .toList();
        return new ResponseEntity<>(new BroadcastListOutput().bList(broadcastsApiModelList), HttpStatus.OK);
    }
}
