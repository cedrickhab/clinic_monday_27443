package kigali.clinc.clinicmanagement.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.service.AppointmentService;

@RestController
@RequestMapping({"/api/appointments", "/api/appointment"})
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/save")
    public ResponseEntity<?> saveAppointment(@RequestBody Appointment appointment) {
        String message = appointmentService.saveAppointment(appointment);
        if (message.equals("Doctor is already booked on that date")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
        if (message.equals("Patient or doctor not found")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(appointment, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable UUID id) {
        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);

        if (appointment.isPresent()) {
            return ResponseEntity.ok(appointment.get());
        }

        return new ResponseEntity<>("Appointment not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable UUID id,
            @RequestBody Appointment appointment) {
        Optional<Appointment> updatedAppointment = appointmentService.updateAppointment(id, appointment);

        if (updatedAppointment.isPresent()) {
            return ResponseEntity.ok(updatedAppointment.get());
        }

        return new ResponseEntity<>("Appointment, patient or doctor not found", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable UUID id) {
        String message = appointmentService.deleteAppointment(id);
        if (message.equals("Appointment is deleted successfully")) {
            return ResponseEntity.ok(message);
        }

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/by-status")
    public List<Appointment> findByStatus(@RequestParam AppointmentStatus status) {
        return appointmentService.findByStatus(status);
    }

    @GetMapping("/between")
    public List<Appointment> findBetween(@RequestParam String start, @RequestParam String end) {
        return appointmentService.findBetween(LocalDate.parse(start), LocalDate.parse(end));
    }

    @GetMapping("/stats/by-status")
    public List<Object[]> countByStatus() {
        return appointmentService.countByStatus();
    }

    @PatchMapping("/cancel-day")
    public String cancelDay(@RequestParam UUID doctorId, @RequestParam String date) {
        return appointmentService.cancelDay(doctorId, LocalDate.parse(date));
    }

    @GetMapping("/page")
    public Page<Appointment> findPage(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "appointmentDate,desc") String sort) {
        String[] parts = sort.split(",");
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length > 1) {
            direction = Sort.Direction.fromString(parts[1]);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, parts[0]));
        return appointmentService.findPage(pageable);
    }

    @DeleteMapping("/cancelled-before")
    public String deleteCancelledBefore(@RequestParam String date) {
        return appointmentService.deleteCancelledBefore(LocalDate.parse(date));
    }

}
