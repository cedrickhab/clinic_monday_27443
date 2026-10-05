package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepo;

    public Optional<Patient> savePatient(Patient patient) {
        return Optional.of(patientRepo.save(patient));
    }

    public List<Patient> getAllPatients() {
        return patientRepo.findAll();
    }

    public Optional<Patient> getPatientById(UUID id) {
        return patientRepo.findById(id);
    }

    public Optional<Patient> updatePatient(UUID id, Patient newPatient) {
        Optional<Patient> foundPatient = patientRepo.findById(id);

        if (foundPatient.isEmpty()) {
            return Optional.empty();
        }

        Patient patient = foundPatient.get();
        patient.setFirstName(newPatient.getFirstName());
        patient.setLastName(newPatient.getLastName());
        patient.setDateOfBirth(newPatient.getDateOfBirth());

        return Optional.of(patientRepo.save(patient));
    }

    public String deletePatient(UUID id) {
        if (!patientRepo.existsById(id)) {
            return "Patient not found";
        }

        patientRepo.deleteById(id);
        return "Patient is deleted successfully";
    }

    public List<Patient> findByLastName(String lastName) {
        return patientRepo.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    public List<Patient> findPatientsOfDoctor(UUID doctorId) {
        return patientRepo.findPatientsOfDoctor(doctorId);
    }

    public List<Patient> findFrequentPatients(long min) {
        return patientRepo.findFrequentPatients(min);
    }

}
