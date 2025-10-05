// src/main/java/com/project/posgunstore/Realtime/Controller/RealtimeController.java
package com.project.posgunstore.Realtime.Controller;

import com.project.posgunstore.Realtime.Events.InventoryEventPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController
@RequestMapping("/api/realtime")
@Slf4j
public class RealtimeController {

  private static final Long TIMEOUT_MS = 0L; // never time out (client should reconnect)
  private final Set<SseEmitter> emitters = new CopyOnWriteArraySet<>();

  @GetMapping(path = "/inventory-sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter connect() {
    SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
    emitters.add(emitter);

    emitter.onCompletion(() -> emitters.remove(emitter));
    emitter.onTimeout(() -> emitters.remove(emitter));
    emitter.onError(e -> emitters.remove(emitter));

    // optional hello ping
    try { emitter.send(SseEmitter.event().name("hello").data("connected")); }
    catch (IOException ignored) { }

    return emitter;
  }

  // called from RealtimeBridge
  public void broadcast(InventoryEventPayload payload) {
    emitters.forEach(em -> {
      try {
        em.send(SseEmitter.event().name("inventory").data(payload));
      } catch (IOException ex) {
        em.complete();
        emitters.remove(em);
      }
    });
  }
}
