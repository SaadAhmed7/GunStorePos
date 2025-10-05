// SerialsStatusRow.java
package com.project.posgunstore.Reports.DTO;

public interface SerialsStatusRow {
  Long getProductId();
  String getSku();
  String getProductName();
  Integer getAvailable();
  Integer getSold();
  Integer getDamaged();
}
