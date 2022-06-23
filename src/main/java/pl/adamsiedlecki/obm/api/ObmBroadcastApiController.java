package pl.adamsiedlecki.obm.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.api.BroadcastsApi;
import org.openapitools.model.BroadcastInfoInput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@Slf4j
public class ObmBroadcastApiController implements BroadcastsApi {

    @Override
    public ResponseEntity<Void> processBroadcastInformation(BroadcastInfoInput broadcastInfoInput) {
        log.info("Broadcast information received: {}", broadcastInfoInput);
        if (StringUtils.isBlank(broadcastInfoInput.getText())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
