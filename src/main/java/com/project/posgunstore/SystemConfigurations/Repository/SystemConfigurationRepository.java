package com.project.posgunstore.SystemConfigurations.Repository;

import com.project.posgunstore.SystemConfigurations.Model.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Long> {
    List<SystemConfiguration> findByConfigType(String configType);
    Optional<SystemConfiguration> findByConfigTypeAndConfigKey(String configType, String configKey);
}
