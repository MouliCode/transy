package com.transfer.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.common.constants.MessageTypes;
import com.transfer.common.dto.SignalDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommonLibTest {

    @Test
    void constantsAreDefined() {
        assertNotNull(MessageTypes.REGISTER);
        assertNotNull(MessageTypes.OFFER);
        assertNotNull(MessageTypes.TRANSFER_COMPLETED);
    }

    @Test
    void signalDtoSerializesAndDeserializes() throws Exception {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        SignalDTO dto = new SignalDTO("OFFER", "a", "b", "sdp");

        String json = mapper.writeValueAsString(dto);
        SignalDTO parsed = mapper.readValue(json, SignalDTO.class);

        assertEquals(dto, parsed);
    }
}
