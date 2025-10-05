// src/main/java/com/project/posgunstore/Alerts/Repository/AlertSubscriptionRepository.java
package com.project.posgunstore.Alerts.Repository;

import com.project.posgunstore.Alerts.Model.AlertSubscription;
import com.project.posgunstore.Alerts.Model.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertSubscriptionRepository extends JpaRepository<AlertSubscription, Long> {
  List<AlertSubscription> findByAlertTypeAndActiveTrue(AlertType type);
}
