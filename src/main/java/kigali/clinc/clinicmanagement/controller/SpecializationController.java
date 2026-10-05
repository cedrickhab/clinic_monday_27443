package kigali.clinc.clinicmanagement.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.service.SpecializationService;

@RestController
@RequestMapping({"/api/specializations", "/api/specialization"})
public class SpecializationController {

    @Autowired
    private SpecializationService specializationService;

    @PostMapping("/save")
    public ResponseEntity<?> saveSpecialization(@RequestBody Specialization specialization) {
        Optional<Specialization> savedSpecialization = specializationService.saveSpecialization(specialization);

        if (savedSpecialization.isPresent()) {
            return new ResponseEntity<>(savedSpecialization.get(), HttpStatus.CREATED);
        }

        return new ResponseEntity<>("Doctor not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Specialization>> getAllSpecializations() {
        return ResponseEntity.ok(specializationService.getAllSpecializations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSpecializationById(@PathVariable UUID id) {
        Optional<Specialization> specialization = specializationService.getSpecializationById(id);

        if (specialization.isPresent()) {
            return ResponseEntity.ok(specialization.get());
        }

        return new ResponseEntity<>("Specialization not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSpecialization(
            @PathVariable UUID id,
            @RequestBody Specialization specialization) {
        Optional<Specialization> updatedSpecialization = specializationService.updateSpecialization(id, specialization);

        if (updatedSpecialization.isPresent()) {
            return ResponseEntity.ok(updatedSpecialization.get());
        }

        return new ResponseEntity<>("Specialization or doctor not found", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSpecialization(@PathVariable UUID id) {
        String message = specializationService.deleteSpecialization(id);
        if (message.equals("Specialization is deleted successfully")) {
            return ResponseEntity.ok("Specialization is deleted successfully");
        }

        return new ResponseEntity<>("Specialization not found", HttpStatus.NOT_FOUND);
    }

}
