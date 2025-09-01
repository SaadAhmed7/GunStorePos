package com.project.posgunstore.Station.Controller;

import com.project.posgunstore.Station.Model.Station;
import com.project.posgunstore.Station.Service.StationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    public List<Station> getAll() {
        return stationService.getAll();
    }

    @GetMapping("/{id}")
    public Station getById(@PathVariable UUID id) {
        return stationService.getById(id);
    }

    @PostMapping
    public Station create(@RequestBody Station station) {
        return stationService.create(station);
    }

    @PutMapping("/{id}")
    public Station update(@PathVariable UUID id, @RequestBody Station station) {
        return stationService.update(id, station);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        stationService.delete(id);
    }
}
