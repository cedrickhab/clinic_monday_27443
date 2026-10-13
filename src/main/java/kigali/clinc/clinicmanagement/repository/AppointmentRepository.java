package kigali.clinc.clinicmanagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository
        extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    List<Appointment> findByPatientSsNumberAndStatusInAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAsc(String ssNumber, List<AppointmentStatus> statuses, LocalDate date);

    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusNot(UUID doctorId, LocalDate date, AppointmentStatus status);

    boolean existsByDoctorIdAndStatusNotAndAppointmentDate(UUID doctorId, AppointmentStatus status, LocalDate date);

    long countByStatus(AppointmentStatus status);

    long countByAppointmentDate(LocalDate date);

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(LocalDate start, LocalDate end);

    boolean existsByDoctorIdAndAppointmentDateAndStatusNot(UUID doctorId, LocalDate date, AppointmentStatus status);

    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countByStatus();

    @Modifying
    @Query("UPDATE Appointment a SET a.status = :cancelled WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.status <> :completed")
    int cancelDay(@Param("doctorId") UUID doctorId, @Param("date") LocalDate date,
            @Param("cancelled") AppointmentStatus cancelled, @Param("completed") AppointmentStatus completed);

    @Modifying
    @Query("DELETE FROM Appointment a WHERE a.status = :status AND a.appointmentDate < :date")
    int deleteCancelledBefore(@Param("date") LocalDate date, @Param("status") AppointmentStatus status);

}
