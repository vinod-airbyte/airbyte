package io.airbyte.integrations.standardtest.destination;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.airbyte.protocol.models.AirbyteMessage;
import io.airbyte.protocol.models.AirbyteMessage.Type;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Modifier {

  default void modifyData(List<AirbyteMessage> messages, Map<String, String> datesField)
      throws IOException {
    for (AirbyteMessage message : messages) {
      if (message.getType() == Type.RECORD) {
        modify((ObjectNode) message.getRecord().getData(), datesField);
      }
    }
  }

  default void modify(ObjectNode data, Map<String, String> datesField) throws IOException {}

  default boolean requiresDateTimeModification() {
    return false;
  }

}
