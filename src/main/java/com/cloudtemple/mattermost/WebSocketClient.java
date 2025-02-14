package com.cloudtemple.mattermost;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;
import com.cloudtemple.mattermost.traders.Event;
import lombok.extern.slf4j.Slf4j;

/**
 * Websocket client.
 */
@Slf4j
class WebSocketClient {

  private final static String _authorization = "Authorization";
  private final AsyncHttpClient _client;
  private final String _url;
  private final String _bearer;
  private volatile Optional<WsSocketListener> _listener = Optional.empty();
  private volatile WebSocket _websocket;
  private boolean _reconnectionEnable = true;
  private static ExecutorService reconnectionExecutor = Executors.newSingleThreadExecutor();
  private final WsSocketListener _reconnectListener = new WsSocketListener() {
    @Override
    public void onClose(final WebSocket ws, int code, String reason) {
      log.debug("[ws] closed, code {}, reason {}", code, reason);
      reconnectionExecutor.submit(() -> {
        log.debug("[ws] checking reconnect");
        int retryCount = 0;
        int retryDelaySeconds = 1;
        while (_reconnectionEnable && (_websocket == null || !_websocket.isOpen())) {
          try {
            connect();
          } catch (final Exception e) {
            if (retryCount == 0) {
              log.error("Disconnected; error on first retry: " + e.getMessage());
            }
            if (retryCount == 1) {
              log.error("Can't reconnect", e);
            }
            retryCount++;
            if (retryCount % 10 == 0) {
              log.debug("continuing to try to reconnect...");
            }
            if (retryCount == 100) {
              retryDelaySeconds = 10;
            }
            try {
              TimeUnit.SECONDS.sleep(retryDelaySeconds);
            } catch (Exception ex2) {
            }
          }
        }
      });

      /*
       * boolean reconnected = false; int retryCount = 0; while (_reconnectionEnable && !
       * reconnected) { try { connect(); reconnected = true; } catch (final Exception e) {
       * retryCount++; if (retryCount == 10) { log.log(Level.SEVERE, "Can't reconnect", e); } else
       * if (retryCount % 10 == 0) { log.log(Level.SEVERE, "Still can't reconnect after " +
       * retryCount + " seconds."); } try { TimeUnit.SECONDS.sleep(1); } catch (Exception ex2) { } }
       * }
       */
    }

    @Override
    public void onEvent(final Event event) {
      // Truly don't care.
    }
  };

  private WebSocketClient(final String url_, final String bearer, final AsyncHttpClient client_) {
    _client = client_;
    _url = url_;
    _bearer = bearer;
  }

  protected WebSocketClient(final String url_, final String bearer,
      final Optional<WsSocketListener> listener) {
    this(url_, bearer, new DefaultAsyncHttpClient());
    _listener = listener;
  }

  protected WebSocketClient(final String url_, final String bearer) {
    this(url_, bearer, new DefaultAsyncHttpClient());
  }

  public void connect() throws InterruptedException, ExecutionException {
    if (_listener.isPresent()) {
      _websocket = _client.prepareGet(_url)//
          .setHeader(_authorization, _bearer)//
          .execute(new WebSocketUpgradeHandler.Builder()//
              .addWebSocketListener(_listener.get())//
              .addWebSocketListener(_reconnectListener)//
              .build()//
          ).get();
    }
  }

  public void connect(final WsSocketListener listener)
      throws InterruptedException, ExecutionException {
    _listener = Optional.ofNullable(listener);
    connect();
  }

  public void sendMessage(final String message) {
    _websocket.sendTextFrame(message);
  }

  public void sendMessage(final byte[] message) {
    log.debug("[ws] received --> " + message);
    _websocket.sendBinaryFrame(message);
  }

  public void close() throws IOException {
    _reconnectionEnable = false;
    log.debug("close()");
    _websocket.sendCloseFrame();
    _client.close();
  }


}
