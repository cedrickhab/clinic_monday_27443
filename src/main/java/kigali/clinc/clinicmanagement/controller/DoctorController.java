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

import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.service.DoctorService;

@RestController
@RequestMapping({"/api/doctors", "/api/doctor"})
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping("/save")
    public ResponseEntity<?> saveDoctor(@RequestBody Doctor doctor) {
        Optional<Doctor> savedDoctor = doctorService.saveDoctor(doctor);

        if (savedDoctor.isPresent()) {
            return new ResponseEntity<>(savedDoctor.get(), HttpStatus.CREATED);
        }

        return new ResponseEntity<>("Office not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable UUID id) {
        Optional<Doctor> doctor = doctorService.getDoctorById(id);

        if (doctor.isPresent()) {
            return ResponseEntity.ok(doctor.get());
        }

        return new ResponseEntity<>("Doctor not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDoctor(
            @PathVariable UUID id,
            @RequestBody Doctor doctor) {
        Optional<Doctor> updatedDoctor = doctorService.updateDoctor(id, doctor);

        if (updatedDoctor.isPresent()) {
            return ResponseEntity.ok(updatedDoctor.get());
        }

        return new ResponseEntity<>("Doctor or office not found", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDoctor(@PathVariable UUID id) {
        String message = doctorService.deleteDoctor(id);
        if (message.equals("Doctor is deleted successfully")) {
            return ResponseEntity.ok("Doctor is deleted successfully");
        }

        return new ResponseEntity<>("Doctor not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/by-specialization")
    public List<Doctor> findBySpecialization(@RequestParam String name) {
        return doctorService.findBySpecialization(name);
    }

    @GetMapping("/without-office")
    public List<Doctor> findWithoutOffice() {
        return doctorService.findWithoutOffice();
    }

}
