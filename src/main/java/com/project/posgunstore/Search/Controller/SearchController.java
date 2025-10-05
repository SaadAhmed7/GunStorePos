// src/main/java/com/project/posgunstore/Search/Controller/SearchController.java
package com.project.posgunstore.Search.Controller;

import com.project.posgunstore.Search.DTO.SearchResponse;
import com.project.posgunstore.Search.DTO.SearchScope;
import com.project.posgunstore.Search.Service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

  private final SearchService service;

  @GetMapping
  public SearchResponse search(@RequestParam String q,
                               @RequestParam(defaultValue = "all") String scope,
                               @RequestParam(defaultValue = "10") int limit) {
    return service.search(q, SearchScope.of(scope), limit);
  }
}
