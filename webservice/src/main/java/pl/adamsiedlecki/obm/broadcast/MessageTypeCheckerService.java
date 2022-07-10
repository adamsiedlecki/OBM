package pl.adamsiedlecki.obm.broadcast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.examples.Utils;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.BroadcastInfoInput;
import org.springframework.stereotype.Service;
import pl.adamsiedlecki.obm.dto.MessageTypeEnumDto;
import pl.adamsiedlecki.obm.files.Resources;

import java.io.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageTypeCheckerService {

    private final ObjectMapper mapper;

    final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

    public MessageTypeEnumDto check(String text) {
        try {
            final JsonNode subjectUnderCheck = mapper.readTree(text);
            if (isJsonSchemaValidationSuccessful(subjectUnderCheck, "json-schema/temperature-request-schema.json")) {
                return MessageTypeEnumDto.TEMPERATURE_REQUEST;
            }
            if (isJsonSchemaValidationSuccessful(subjectUnderCheck, "json-schema/temperature-response-schema.json")) {
                return MessageTypeEnumDto.TEMPERATURE_RESPONSE;
            }
            if (isJsonSchemaValidationSuccessful(subjectUnderCheck, "json-schema/humidity-request-schema.json")) {
                return MessageTypeEnumDto.HUMIDITY_REQUEST;
            }
            if (isJsonSchemaValidationSuccessful(subjectUnderCheck, "json-schema/humidity-response-schema.json")) {
                return MessageTypeEnumDto.HUMIDITY_RESPONSE;
            }
            if (isJsonSchemaValidationSuccessful(subjectUnderCheck, "json-schema/voltage-request-schema.json")) {
                return MessageTypeEnumDto.VOLTAGE_REQUEST;
            }
            if (isJsonSchemaValidationSuccessful(subjectUnderCheck, "json-schema/voltage-response-schema.json")) {
                return MessageTypeEnumDto.VOLTAGE_RESPONSE;
            }
            if (isJsonSchemaValidationSuccessful(subjectUnderCheck, "json-schema/gps-broadcast-schema.json")) {
                return MessageTypeEnumDto.GPS_BROADCAST;
            }


        } catch (Exception e) {
            log.error("Unexpected error while checking schema: {}", e.getMessage());
        }

        return MessageTypeEnumDto.UNKNOWN;
    }

    private boolean isJsonSchemaValidationSuccessful(JsonNode subjectUnderCheck, String schemaName) {
        try {
            String schemaString = Resources.getResourceFileAsString(schemaName);
            final JsonNode schemaNode = mapper.readTree(schemaString);
            final JsonSchema schema = factory.getJsonSchema(schemaNode);
            ProcessingReport report = schema.validate(subjectUnderCheck);
            return report.isSuccess();
        } catch (IOException | ProcessingException e) {
            log.error("Error while checking compatibility with temperature request schema ", e);
        }
        return false;
    }

}
