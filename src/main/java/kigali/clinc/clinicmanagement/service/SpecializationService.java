package kigali.clinc.clinicmanagement.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.SpecializationRepository;

@Service
public class SpecializationService {
    @Autowired
    private SpecializationRepository specializationRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    public Optional<Specialization> saveSpecialization(Specialization specialization) {
        if (!loadDoctors(specialization)) {
            return Optional.empty();
        }
        return Optional.of(specializationRepo.save(specialization));
    }

    public List<Specialization> getAllSpecializations() {
        return specializationRepo.findAll();
    }

    public Optional<Specialization> getSpecializationById(UUID id) {
        return specializationRepo.findById(id);
    }

    public Optional<Specialization> updateSpecialization(UUID id, Specialization newSpecialization) {
        Optional<Specialization> found = specializationRepo.findById(id);
        if (found.isEmpty() || !loadDoctors(newSpecialization)) {
            return Optional.empty();
        }
        Specialization specialization = found.get();
        specialization.setName(newSpecialization.getName());
        specialization.setDoctors(newSpecialization.getDoctors());
        return Optional.of(specializationRepo.save(specialization));
    }

    public String deleteSpecialization(UUID id) {
        if (!specializationRepo.existsById(id)) {
            return "Specialization not found";
        }
        specializationRepo.deleteById(id);
        return "Specialization is deleted successfully";
    }

    private boolean loadDoctors(Specialization specialization) {
        List<Doctor> doctors = new ArrayList<>();
        if (specialization.getDoctors() != null) {
            for (Doctor doctor : specialization.getDoctors()) {
                if (doctor == null || doctor.getId() == null) {
                    return false;
                }
                Optional<Doctor> found = doctorRepo.findById(doctor.getId());
                if (found.isEmpty()) {
                    return false;
                }
                doctors.add(found.get());
            }
        }
        specialization.setDoctors(doctors);
        return true;
    }

}
