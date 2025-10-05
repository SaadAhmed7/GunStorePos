// src/main/java/com/project/posgunstore/Serials/Controller/SerialController.java
package com.project.posgunstore.Serials.Controller;

import com.project.posgunstore.Serials.DTO.*;
import com.project.posgunstore.Serials.Model.SerialStatus;
import com.project.posgunstore.Serials.Service.SerialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/serials")
@RequiredArgsConstructor
public class SerialController {

  private final SerialService service;

  // GET /api/serials/{serialId}
  @GetMapping("/{serialId}")
  public SerialResponse get(@PathVariable Long serialId) {
    return service.get(serialId);
  }

  // PUT /api/serials/{serialId}?status=...  (here we accept body for version & optional move)
  @PutMapping("/{serialId}")
  public SerialResponse setStatus(@PathVariable Long serialId,
                                  @Valid @RequestBody SerialUpdateRequest req) {
    return service.updateStatus(serialId, req);
  }

  // GET /api/serials/search?q=...
  @GetMapping("/search")
  public Page<SerialResponse> search(@RequestParam(required = false) String q,
                                     @RequestParam(required = false) Long productId,
                                     @RequestParam(required = false) SerialStatus status,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "50") int size) {
    return service.search(q, productId, status, page, size);
  }

  // GET /api/serials/verify/{serialNumber}
  @GetMapping("/verify/{serialNumber}")
  public SerialVerifyResponse verify(@PathVariable String serialNumber) {
    return service.verify(serialNumber);
  }

  // GET /api/serials/{serialId}/history
  @GetMapping("/{serialId}/history")
  public List<SerialHistoryItem> history(@PathVariable Long serialId) {
    return service.history(serialId);
  }

  // POST /api/serials/{serialId}/compliance-events
  @PostMapping("/{serialId}/compliance-events")
  @ResponseStatus(HttpStatus.CREATED)
  public void addCompliance(@PathVariable Long serialId,
                            @Valid @RequestBody ComplianceEventRequest req) {
    service.addComplianceEvent(serialId, req);
  }
}
