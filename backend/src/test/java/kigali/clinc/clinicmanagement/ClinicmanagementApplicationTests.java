package kigali.clinc.clinicmanagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.service.DoctorService;
import kigali.clinc.clinicmanagement.service.OfficeService;

@Transactional
@SpringBootTest
class ClinicmanagementApplicationTests {

	@Autowired
	private DoctorService doctorService;

	@Autowired
	private OfficeService officeService;

	@Test
	void contextLoads() {
	}

	@Test
	void deletingDoctorReleasesItsOffice() {
		Office office = new Office();
		office.setName("Test office");
		office.setOfficeNumber(801);
		assertEquals("Office is saved successfully", officeService.saveOffice(office));

		Doctor doctor = new Doctor();
		doctor.setFirstName("Test");
		doctor.setLastName("Doctor");
		doctor.setOffice(office);
		Doctor savedDoctor = doctorService.saveDoctor(doctor).orElseThrow();

		assertEquals("Doctor is deleted successfully", doctorService.deleteDoctor(savedDoctor.getId()));
		assertEquals("Office is deleted successfully", officeService.deleteOffice(office.getId()));
	}

	@Test
	void assignedOfficeCannotBeDeleted() {
		Office office = new Office();
		office.setName("Assigned office");
		office.setOfficeNumber(802);
		assertEquals("Office is saved successfully", officeService.saveOffice(office));

		Doctor doctor = new Doctor();
		doctor.setFirstName("Assigned");
		doctor.setLastName("Doctor");
		doctor.setOffice(office);
		doctorService.saveDoctor(doctor).orElseThrow();

		assertEquals("Office is assigned to a doctor", officeService.deleteOffice(office.getId()));
	}

}
