// src/main/java/com/project/posgunstore/Search/Service/SearchService.java
package com.project.posgunstore.Search.Service;

import com.project.posgunstore.Search.DTO.SearchResponse;
import com.project.posgunstore.Search.DTO.SearchScope;

public interface SearchService {
  SearchResponse search(String q, SearchScope scope, int limit);
}
