package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Prescription;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    public String savePrescription(UUID appointmentId, Prescription prescription) {
        Optional<Appointment> foundAppointment = appointmentRepo.findById(appointmentId);

        if (foundAppointment.isEmpty()) {
            return "The appointment with that id does not exist";
        }

        Appointment appointment = foundAppointment.get();

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            return "Prescriptions are only for COMPLETED appointments";
        }

        prescription.setIssuedDate(LocalDate.now());
        prescription.setAppointment(appointment);
        prescriptionRepo.save(prescription);

        return "Prescription saved successfully";
    }

    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepo.findAll();
    }

    public List<Prescription> findByAppointment(UUID appointmentId) {
        Optional<Appointment> foundAppointment = appointmentRepo.findById(appointmentId);

        if (foundAppointment.isEmpty()) {
            return null;
        }

        return prescriptionRepo.findByAppointment(foundAppointment.get());
    }

    public List<Prescription> findByPatientSsNumber(String ssNumber) {
        return prescriptionRepo.findByPatientSsNumberOrderByIssuedDateDesc(ssNumber);
    }

    public String updatePrescription(UUID id, Prescription newPrescription) {
        Optional<Prescription> foundPrescription = prescriptionRepo.findById(id);

        if (foundPrescription.isEmpty()) {
            return "The prescription with that id does not exist";
        }

        Prescription prescription = foundPrescription.get();
        prescription.setMedicineName(newPrescription.getMedicineName());
        prescription.setDosage(newPrescription.getDosage());
        prescription.setDurationDays(newPrescription.getDurationDays());
        prescriptionRepo.save(prescription);

        return "Prescription updated successfully";
    }

    public String deletePrescription(UUID id) {
        if (!prescriptionRepo.existsById(id)) {
            return "The prescription with that id does not exist";
        }

        prescriptionRepo.deleteById(id);
        return "Prescription deleted successfully";
    }

    public List<Object[]> countByMedicine() {
        return prescriptionRepo.countByMedicine();
    }

}
