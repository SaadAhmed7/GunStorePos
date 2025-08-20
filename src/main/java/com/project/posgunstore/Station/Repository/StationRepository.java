package com.project.posgunstore.Station.Repository;

import com.project.posgunstore.Station.Model.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StationRepository extends JpaRepository<Station, UUID> {}