package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private OfficeRepository officeRepo;

    public Optional<Doctor> saveDoctor(Doctor doctor) {
        if (!loadOffice(doctor)) {
            return Optional.empty();
        }

        return Optional.of(doctorRepo.save(doctor));
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepo.findAll();
    }

    public Optional<Doctor> getDoctorById(UUID id) {
        return doctorRepo.findById(id);
    }

    public Optional<Doctor> updateDoctor(UUID id, Doctor newDoctor) {
        Optional<Doctor> foundDoctor = doctorRepo.findById(id);

        if (foundDoctor.isEmpty() || !loadOffice(newDoctor)) {
            return Optional.empty();
        }

        Doctor doctor = foundDoctor.get();
        doctor.setFirstName(newDoctor.getFirstName());
        doctor.setLastName(newDoctor.getLastName());
        doctor.setDateOfBirth(newDoctor.getDateOfBirth());
        doctor.setOffice(newDoctor.getOffice());

        return Optional.of(doctorRepo.save(doctor));
    }

    public String deleteDoctor(UUID id) {
        if (!doctorRepo.existsById(id)) {
            return "Doctor not found";
        }

        doctorRepo.deleteById(id);
        return "Doctor is deleted successfully";
    }

    private boolean loadOffice(Doctor doctor) {
        if (doctor.getOffice() == null) {
            return true;
        }

        if (doctor.getOffice().getId() == null) {
            return false;
        }

        Optional<Office> office = officeRepo.findById(doctor.getOffice().getId());

        if (office.isEmpty()) {
            return false;
        }

        doctor.setOffice(office.get());
        return true;
    }

    public List<Doctor> findBySpecialization(String name) {
        return doctorRepo.findBySpecialization(name);
    }

    public List<Doctor> findWithoutOffice() {
        return doctorRepo.findWithoutOffice();
    }

}
