package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.PatientRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final PrescriptionRepository prescriptionRepo;

    public AppointmentService(
            AppointmentRepository appointmentRepo,
            PatientRepository patientRepo,
            DoctorRepository doctorRepo,
            PrescriptionRepository prescriptionRepo) {
        this.appointmentRepo = appointmentRepo;
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
        this.prescriptionRepo = prescriptionRepo;
    }

    public String saveAppointment(Appointment appointment) {
        if (!resolvePatientAndDoctor(appointment)) {
            return "Patient or doctor not found";
        }
        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }
        if (appointmentRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(), appointment.getAppointmentDate(), AppointmentStatus.CANCELLED)) {
            return "Doctor is already booked on that date";
        }
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepo.save(appointment);
        return "Appointment is saved successfully";
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepo.findAll();
    }

    public Optional<Appointment> getAppointmentById(UUID id) {
        return appointmentRepo.findById(id);
    }

    public Optional<Appointment> updateAppointment(
            UUID id,
            Appointment newAppointment) {
        Optional<Appointment> foundAppointment = appointmentRepo.findById(id);

        if (foundAppointment.isEmpty()) {
            return Optional.empty();
        }

        if (!resolvePatientAndDoctor(newAppointment)) {
            return Optional.empty();
        }

        Appointment appointment = foundAppointment.get();

        appointment.setAppointmentDate(newAppointment.getAppointmentDate());

        appointment.setReason(newAppointment.getReason());
        appointment.setPatient(newAppointment.getPatient());
        appointment.setDoctor(newAppointment.getDoctor());

        return Optional.of(appointmentRepo.save(appointment));
    }

    public String deleteAppointment(UUID id) {
        if (!appointmentRepo.existsById(id)) {
            return "Appointment not found";
        }

        if (prescriptionRepo.existsByAppointmentId(id)) {
            return "That appointment has prescriptions, delete them first";
        }

        appointmentRepo.deleteById(id);
        return "Appointment is deleted successfully";
    }

    public String updateStatus(UUID id, AppointmentStatus newStatus) {
        Optional<Appointment> foundAppointment = appointmentRepo.findById(id);

        if (foundAppointment.isEmpty()) {
            return "The appointment with that id does not exist";
        }

        Appointment appointment = foundAppointment.get();
        AppointmentStatus current = appointment.getStatus();

        boolean allowed = (current == AppointmentStatus.SCHEDULED
                && (newStatus == AppointmentStatus.CONFIRMED || newStatus == AppointmentStatus.CANCELLED))
                || (current == AppointmentStatus.CONFIRMED
                        && (newStatus == AppointmentStatus.COMPLETED || newStatus == AppointmentStatus.CANCELLED));

        if (!allowed) {
            return "Cannot change status from " + current + " to " + newStatus;
        }

        appointment.setStatus(newStatus);
        appointmentRepo.save(appointment);

        return "Appointment status updated successfully";
    }

    public String reschedule(UUID id, LocalDate newDate) {
        Optional<Appointment> foundAppointment = appointmentRepo.findById(id);

        if (foundAppointment.isEmpty()) {
            return "The appointment with that id does not exist";
        }

        Appointment appointment = foundAppointment.get();

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            return "A COMPLETED appointment cannot be rescheduled";
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            return "A CANCELLED appointment cannot be rescheduled";
        }

        if (newDate.isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }

        if (appointmentRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(), newDate, AppointmentStatus.CANCELLED)) {
            return "Doctor is already booked on that date";
        }

        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepo.save(appointment);

        return "Appointment rescheduled successfully";
    }

    private boolean resolvePatientAndDoctor(Appointment appointment) {
        if (appointment.getPatient() == null ||
                appointment.getPatient().getId() == null ||
                appointment.getDoctor() == null ||
                appointment.getDoctor().getId() == null) {
            return false;
        }

        Optional<Patient> patient = patientRepo.findById(appointment.getPatient().getId());

        Optional<Doctor> doctor = doctorRepo.findById(appointment.getDoctor().getId());

        if (patient.isEmpty() || doctor.isEmpty()) {
            return false;
        }

        appointment.setPatient(patient.get());
        appointment.setDoctor(doctor.get());

        return true;
    }

    public List<Appointment> findByStatus(AppointmentStatus status) {
        return appointmentRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> findBetween(LocalDate start, LocalDate end) {
        return appointmentRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    public List<Object[]> countByStatus() {
        return appointmentRepo.countByStatus();
    }

    @Transactional
    public String cancelDay(UUID doctorId, LocalDate date) {
        int count = appointmentRepo.cancelDay(doctorId, date, AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED);
        return count + " appointments cancelled";
    }

    public Page<Appointment> findPage(Pageable pageable) {
        return appointmentRepo.findAll(pageable);
    }

    @Transactional
    public String deleteCancelledBefore(LocalDate date) {
        int count = appointmentRepo.deleteCancelledBefore(date, AppointmentStatus.CANCELLED);
        return count + " appointments deleted";
    }

}
