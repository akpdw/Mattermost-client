package com.cloudtemple.mattermost;

import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import com.cloudtemple.mattermost.traders.Event;
import com.fasterxml.jackson.databind.MappingJsonFactory;


public interface WsSocketListener extends WebSocketListener {
  org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WsSocketListener.class);

  @Override
  default public void onOpen(final WebSocket ws) {
    log.debug("[ws] opened");
  }

  @Override
  default public void onClose(final WebSocket ws, int code, String reason) {
    log.debug("[ws] closed");
  }

  @Override
  default public void onError(final Throwable t) {
    // t.printStackTrace();
    log.info("[ws] error: " + t);
  }

  @Override
  default public void onBinaryFrame(final byte[] message, boolean finalFragment, int rsv) {
    onTextFrame(new String(message), finalFragment, rsv);
  }

  @Override
  default public void onTextFrame(final String message, boolean finalFragment, int rsv) {
    try {
      onEvent(new MappingJsonFactory().createJsonParser(message).readValueAs(Event.class));
    } catch (final Exception e) {
      log.error("Can't convert json to Map<String,Object>" + message, e);
      onEvent(new Event());
    }
  }

  public void onEvent(final Event event);
}
