package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;
import kigali.clinc.clinicmanagement.repository.PatientRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class DashboardService {

    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final OfficeRepository officeRepo;
    private final AppointmentRepository appointmentRepo;
    private final PrescriptionRepository prescriptionRepo;

    public DashboardService(
            PatientRepository patientRepo,
            DoctorRepository doctorRepo,
            OfficeRepository officeRepo,
            AppointmentRepository appointmentRepo,
            PrescriptionRepository prescriptionRepo) {
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
        this.officeRepo = officeRepo;
        this.appointmentRepo = appointmentRepo;
        this.prescriptionRepo = prescriptionRepo;
    }

    public Map<String, Long> getSummary() {
        Map<String, Long> summary = new LinkedHashMap<>();

        summary.put("patients", patientRepo.count());
        summary.put("doctors", doctorRepo.count());
        summary.put("freeOffices", (long) officeRepo.findFreeOffices().size());
        summary.put("appointmentsToday", appointmentRepo.countByAppointmentDate(LocalDate.now()));
        summary.put("scheduled", appointmentRepo.countByStatus(AppointmentStatus.SCHEDULED));
        summary.put("prescriptions", prescriptionRepo.count());

        return summary;
    }

}
