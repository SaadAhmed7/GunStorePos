// src/main/java/com/project/posgunstore/Search/DTO/SearchScope.java
package com.project.posgunstore.Search.DTO;

public enum SearchScope {
  PRODUCTS, SERIALS, CATEGORIES, MANUFACTURERS, ALL;

  public static SearchScope of(String raw) {
    if (raw == null || raw.isBlank()) return ALL;
    try { return SearchScope.valueOf(raw.trim().toUpperCase()); }
    catch (IllegalArgumentException ex) { return ALL; }
  }
}
