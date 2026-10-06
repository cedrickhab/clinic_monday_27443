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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.service.DoctorService;
import kigali.clinc.clinicmanagement.service.PatientService;

@RestController
@RequestMapping({"/api/patients", "/api/patient"})
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @PostMapping("/save")
    public ResponseEntity<Patient> savePatient(@RequestBody Patient patient) {
        return new ResponseEntity<>(patientService.savePatient(patient).get(), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable UUID id) {
        Optional<Patient> patient = patientService.getPatientById(id);

        if (patient.isPresent()) {
            return ResponseEntity.ok(patient.get());
        }

        return new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(
            @PathVariable UUID id,
            @RequestBody Patient patient) {
        Optional<Patient> updatedPatient = patientService.updatePatient(id, patient);

        if (updatedPatient.isPresent()) {
            return ResponseEntity.ok(updatedPatient.get());
        }

        return new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePatient(@PathVariable UUID id) {
        String message = patientService.deletePatient(id);
        if (message.equals("Patient is deleted successfully")) {
            return ResponseEntity.ok(message);
        }

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/by-last-name")
    public List<Patient> findByLastName(@RequestParam String lastName) {
        return patientService.findByLastName(lastName);
    }

    @GetMapping("/of-doctor/{doctorId}")
    public ResponseEntity<?> findPatientsOfDoctor(@PathVariable UUID doctorId) {
        if (doctorService.getDoctorById(doctorId).isEmpty()) {
            return new ResponseEntity<>("The doctor with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(patientService.findPatientsOfDoctor(doctorId));
    }

    @GetMapping("/frequent")
    public List<Patient> findFrequentPatients(@RequestParam long min) {
        return patientService.findFrequentPatients(min);
    }

}
