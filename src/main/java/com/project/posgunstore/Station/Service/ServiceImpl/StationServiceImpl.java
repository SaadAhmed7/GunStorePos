package com.project.posgunstore.Station.Service.ServiceImpl;

import com.project.posgunstore.Station.Model.Station;
import com.project.posgunstore.Station.Repository.StationRepository;
import com.project.posgunstore.Station.Service.StationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StationServiceImpl implements StationService {

    private final StationRepository stationRepository;

    public StationServiceImpl(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Override
    public List<Station> getAll() {
        return stationRepository.findAll();
    }

    @Override
    public Station getById(UUID id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station not found with id " + id));
    }

    @Override
    public Station create(Station station) {
        return stationRepository.save(station);
    }

    @Override
    public Station update(UUID id, Station station) {
        Station existing = getById(id);
        existing.setName(station.getName());
        existing.setStatus(station.getStatus());
        existing.setIpAddress(station.getIpAddress());
        existing.setPrinter(station.getPrinter());
        existing.setCashDrawer(station.getCashDrawer());
        existing.setDescription(station.getDescription());
        existing.setStore(station.getStore());
        return stationRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        stationRepository.deleteById(id);
    }
}
