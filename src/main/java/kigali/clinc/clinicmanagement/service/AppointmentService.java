package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    public String saveAppointment(Appointment appointment) {
        if (!loadPatientAndDoctor(appointment)) {
            return "Patient or doctor not found";
        }
        if (appointmentRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(), appointment.getAppointmentDate(), AppointmentStatus.CANCELLED)) {
            return "Doctor is already booked on that date";
        }
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

        Optional<Appointment> foundAppointment =
                appointmentRepo.findById(id);

        if (foundAppointment.isEmpty()) {
            return Optional.empty();
        }

        if (!loadPatientAndDoctor(newAppointment)) {
            return Optional.empty();
        }

        Appointment appointment = foundAppointment.get();

        appointment.setAppointmentDate(
                newAppointment.getAppointmentDate());

        appointment.setReason(newAppointment.getReason());
        appointment.setStatus(newAppointment.getStatus());
        appointment.setPatient(newAppointment.getPatient());
        appointment.setDoctor(newAppointment.getDoctor());

        return Optional.of(appointmentRepo.save(appointment));
    }

    public String deleteAppointment(UUID id) {

        if (!appointmentRepo.existsById(id)) {
            return "Appointment not found";
        }

        appointmentRepo.deleteById(id);
        return "Appointment is deleted successfully";
    }

    private boolean loadPatientAndDoctor(Appointment appointment) {

        if (appointment.getPatient() == null ||
                appointment.getPatient().getId() == null ||
                appointment.getDoctor() == null ||
                appointment.getDoctor().getId() == null) {
            return false;
        }

        Optional<Patient> patient = patientRepo.findById(
                appointment.getPatient().getId());

        Optional<Doctor> doctor = doctorRepo.findById(
                appointment.getDoctor().getId());

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

}
