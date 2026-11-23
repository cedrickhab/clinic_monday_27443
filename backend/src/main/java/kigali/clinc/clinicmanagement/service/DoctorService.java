package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;
import kigali.clinc.clinicmanagement.repository.SpecializationRepository;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepo;
    private final OfficeRepository officeRepo;
    private final SpecializationRepository specializationRepo;
    private final AppointmentRepository appointmentRepo;

    public DoctorService(
            DoctorRepository doctorRepo,
            OfficeRepository officeRepo,
            SpecializationRepository specializationRepo,
            AppointmentRepository appointmentRepo) {
        this.doctorRepo = doctorRepo;
        this.officeRepo = officeRepo;
        this.specializationRepo = specializationRepo;
        this.appointmentRepo = appointmentRepo;
    }

    public Optional<Doctor> saveDoctor(Doctor doctor) {
        if (!resolveOffice(doctor)) {
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

        if (foundDoctor.isEmpty() || !resolveOffice(newDoctor)) {
            return Optional.empty();
        }

        Doctor doctor = foundDoctor.get();
        doctor.setFirstName(newDoctor.getFirstName());
        doctor.setLastName(newDoctor.getLastName());
        doctor.setDateOfBirth(newDoctor.getDateOfBirth());
        doctor.setOffice(newDoctor.getOffice());

        return Optional.of(doctorRepo.save(doctor));
    }

    @Transactional
    public String deleteDoctor(UUID id) {
        Optional<Doctor> foundDoctor = doctorRepo.findById(id);
        if (foundDoctor.isEmpty()) {
            return "Doctor not found";
        }

        Doctor doctor = foundDoctor.get();
        if (doctor.getOffice() != null) {
            doctor.getOffice().setDoctor(null);
            doctor.setOffice(null);
        }
        doctorRepo.delete(doctor);
        return "Doctor is deleted successfully";
    }

    private boolean resolveOffice(Doctor doctor) {
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

    public String addSpecialization(UUID doctorId, UUID specializationId) {
        Optional<Doctor> foundDoctor = doctorRepo.findById(doctorId);
        if (foundDoctor.isEmpty()) {
            return "The doctor with that id does not exist";
        }

        Optional<Specialization> foundSpecialization = specializationRepo.findById(specializationId);
        if (foundSpecialization.isEmpty()) {
            return "That specialization does not exist";
        }

        Doctor doctor = foundDoctor.get();
        Specialization specialization = foundSpecialization.get();

        if (doctor.getSpecializations().contains(specialization)) {
            return "The doctor already has that specialization";
        }

        doctor.getSpecializations().add(specialization);
        specialization.getDoctors().add(doctor);
        specializationRepo.save(specialization);

        return "Specialization added to the doctor";
    }

    public String removeSpecialization(UUID doctorId, UUID specializationId) {
        Optional<Doctor> foundDoctor = doctorRepo.findById(doctorId);
        if (foundDoctor.isEmpty()) {
            return "The doctor with that id does not exist";
        }

        Optional<Specialization> foundSpecialization = specializationRepo.findById(specializationId);
        if (foundSpecialization.isEmpty()) {
            return "That specialization does not exist";
        }

        Doctor doctor = foundDoctor.get();
        Specialization specialization = foundSpecialization.get();

        if (!doctor.getSpecializations().contains(specialization)) {
            return "The doctor does not have that specialization";
        }

        doctor.getSpecializations().remove(specialization);
        specialization.getDoctors().remove(doctor);
        specializationRepo.save(specialization);

        return "Specialization removed from the doctor";
    }

    public String moveDoctorToOffice(UUID doctorId, int officeNumber) {
        Optional<Doctor> foundDoctor = doctorRepo.findById(doctorId);
        if (foundDoctor.isEmpty()) {
            return "The doctor with that id does not exist";
        }

        Office office = officeRepo.findByOfficeNumber(officeNumber);
        if (office == null) {
            return "The office with that office number does not exist";
        }

        Doctor doctor = foundDoctor.get();

        if (doctor.getOffice() == null || !doctor.getOffice().getId().equals(office.getId())) {
            if (doctorRepo.existsByOfficeId(office.getId())) {
                return "That office is taken by other doctor";
            }
        }

        doctor.setOffice(office);
        doctorRepo.save(doctor);

        return "Office changed successfully";
    }

    public List<Appointment> getAgenda(UUID doctorId, LocalDate date) {
        if (doctorRepo.findById(doctorId).isEmpty()) {
            return null;
        }

        return appointmentRepo.findByDoctorIdAndAppointmentDateAndStatusNot(
                doctorId, date, AppointmentStatus.CANCELLED);
    }

    public List<Object[]> findWorkload(LocalDate start, LocalDate end) {
        return doctorRepo.findWorkload(start, end, AppointmentStatus.COMPLETED);
    }

    public List<Doctor> searchDoctors(String name) {
        return doctorRepo.searchByName(name);
    }

}
