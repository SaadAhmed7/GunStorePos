package com.project.posgunstore.Station.Service;

import com.project.posgunstore.Station.Model.Station;

import java.util.List;
import java.util.UUID;

public interface StationService {
    List<Station> getAll();
    Station getById(UUID id);
    Station create(Station station);
    Station update(UUID id, Station station);
    void delete(UUID id);
}
