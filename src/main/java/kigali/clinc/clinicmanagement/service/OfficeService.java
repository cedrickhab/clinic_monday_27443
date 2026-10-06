package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;

@Service
public class OfficeService {

    @Autowired
    private OfficeRepository officeRepo;

    public String saveOffice(Office office) {
        if (officeRepo.existsByOfficeNumber(office.getOfficeNumber())) {
            return "Office with that number already exists";
        }

        officeRepo.save(office);
        return "Office is saved successfully";
    }

    public List<Office> getAllOffices() {
        return officeRepo.findAll();
    }

    public Optional<Office> getOfficeById(UUID id) {
        return officeRepo.findById(id);
    }

    public String updateOffice(UUID id, Office newOffice) {
        Optional<Office> foundOffice = officeRepo.findById(id);

        if (foundOffice.isEmpty()) {
            return "Office not found";
        }

        Office officeWithSameNumber = officeRepo.findByOfficeNumber(newOffice.getOfficeNumber());

        if (officeWithSameNumber != null &&
                !officeWithSameNumber.getId().equals(id)) {
            return "Office with that number already exists";
        }

        Office existingOffice = foundOffice.get();

        existingOffice.setName(newOffice.getName());
        existingOffice.setOfficeNumber(newOffice.getOfficeNumber());

        officeRepo.save(existingOffice);

        return "Office is updated successfully";
    }

    public String deleteOffice(UUID id) {
        if (!officeRepo.existsById(id)) {
            return "Office not found";
        }

        officeRepo.deleteById(id);
        return "Office is deleted successfully";
    }

    public Optional<Object[]> findBusiest() {
        List<Object[]> offices = officeRepo.findBusiest(PageRequest.of(0, 1));
        if (offices.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(offices.get(0));
    }

}
