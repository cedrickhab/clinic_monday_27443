package kigali.clinc.clinicmanagement.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Prescription;
import kigali.clinc.clinicmanagement.service.PrescriptionService;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @PostMapping("/save")
    public ResponseEntity<String> savePrescription(
            @RequestParam UUID appointmentId,
            @RequestBody Prescription prescription) {
        String message = prescriptionService.savePrescription(appointmentId, prescription);

        if (message.equals("Prescription saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        }

        if (message.equals("Prescriptions are only for COMPLETED appointments")) {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> findByAppointment(@PathVariable UUID appointmentId) {
        List<Prescription> prescriptions = prescriptionService.findByAppointment(appointmentId);

        if (prescriptions == null) {
            return new ResponseEntity<>("The appointment with that id does not exist", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/patient/{ssNumber}")
    public List<Prescription> findByPatientSsNumber(@PathVariable String ssNumber) {
        return prescriptionService.findByPatientSsNumber(ssNumber);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updatePrescription(
            @PathVariable UUID id,
            @RequestBody Prescription prescription) {
        String message = prescriptionService.updatePrescription(id, prescription);

        if (message.equals("Prescription updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePrescription(@PathVariable UUID id) {
        String message = prescriptionService.deletePrescription(id);

        if (message.equals("Prescription deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/stats/top-medicines")
    public List<Object[]> findTopMedicines() {
        return prescriptionService.countByMedicine();
    }

}
