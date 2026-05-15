package org.example.web;

import org.example.models.Vehicle;
import org.example.services.VehicleServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController
{
    private final VehicleServiceInterface vehicleService;

    public VehicleController(VehicleServiceInterface vehicleService)
    {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<Vehicle> list(@RequestParam(name = "available", required = false, defaultValue = "false") boolean available)
    {
        if(available)
        {
            return vehicleService.findAvailableVehicles();
        }

        return vehicleService.findAllVehicles();


    }

    @GetMapping("/{id}")
    public Vehicle get(@PathVariable String id)
    {
        return vehicleService.findById(id);
    }

    @PostMapping
    public Vehicle create(@RequestBody Vehicle vehicle)
    {
        return vehicleService.addVehicle(vehicle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id)
    {
        vehicleService.removeVehicle(id);

        return ResponseEntity.noContent().build();
    }

}
