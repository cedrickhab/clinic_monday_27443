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

import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.service.OfficeService;

@RestController
@RequestMapping({"/api/offices", "/api/office"})
public class OfficeController {

    @Autowired
    private OfficeService officeService;

    @PostMapping("/save")
    public ResponseEntity<String> saveOffice(@RequestBody Office office) {
        String message = officeService.saveOffice(office);

        if (message.equals("Office is saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Office>> getAllOffices() {
        return ResponseEntity.ok(officeService.getAllOffices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOfficeById(@PathVariable UUID id) {
        Optional<Office> office = officeService.getOfficeById(id);

        if (office.isPresent()) {
            return ResponseEntity.ok(office.get());
        }

        return new ResponseEntity<>("Office not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateOffice(
            @PathVariable UUID id,
            @RequestBody Office office) {
        String message = officeService.updateOffice(id, office);

        if (message.equals("Office is updated successfully")) {
            return ResponseEntity.ok(message);
        }

        if (message.equals("Office not found")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOffice(@PathVariable UUID id) {
        String message = officeService.deleteOffice(id);

        if (message.equals("Office is deleted successfully")) {
            return ResponseEntity.ok(message);
        }

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/busiest")
    public ResponseEntity<?> findBusiest() {
        Optional<Object[]> office = officeService.findBusiest();
        if (office.isEmpty()) {
            return ResponseEntity.ok("No appointments yet");
        }
        return ResponseEntity.ok(office.get());
    }

}
