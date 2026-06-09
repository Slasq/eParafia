package edu.prz.eparish.pastoralcare.application;

import edu.prz.eparish.pastoralcare.domain.familyaddress.FamilyAddress;
import edu.prz.eparish.pastoralcare.domain.familyaddress.FamilyAddressRepository;
import edu.prz.eparish.pastoralcare.domain.parishioner.Parishioner;
import edu.prz.eparish.pastoralcare.domain.parishioner.ParishionerRepository;
import edu.prz.eparish.pastoralcare.domain.family.Family;
import edu.prz.eparish.pastoralcare.domain.family.FamilyRepository;
import edu.prz.eparish.api.support.ListFilterSupport;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor
public class PastoralCareService {

  private final PastoralCareFactory factory;
  private final FamilyRepository familyRepo;
  private final FamilyAddressRepository addressRepo;
  private final ParishionerRepository parishionerRepo;

  // ── Commands ────────────────────────────────────────────────────────────────

  public record AddFamilyCommand(String familyName, Integer memberCount) {}

  public record AddAddressCommand(
      String street, String houseNumber, String apartmentNumber,
      String postalCode, String city, Long familyId) {}

  public record PatchFamilyCommand(String familyName, Integer memberCount) {}

  public record PatchAddressCommand(
      String street, String houseNumber, String apartmentNumber,
      String postalCode, String city) {}

  // ── UC: Zarządzanie wspólnotą parafialną — dodaj rodzinę ────────────────────

  public Family addFamily(AddFamilyCommand cmd) {
    Family family = factory.createFamily(cmd.familyName(), cmd.memberCount());
    return familyRepo.save(family);
  }

  // ── UC: Zmiana nazwiska rodziny ──────────────────────────────────────────────

  public Family updateFamilyName(Long id, String familyName) {
    Family family = requireFamily(id);
    family.setFamilyName(familyName);
    return familyRepo.save(family);
  }

  // ── UC: Dodaj liczbę członków rodziny ────────────────────────────────────────

  public Family updateMemberCount(Long id, Integer count) {
    Family family = requireFamily(id);
    family.setMemberCount(count);
    return familyRepo.save(family);
  }

  public Family patchFamily(Long id, PatchFamilyCommand cmd) {
    Family family = requireFamily(id);
    if (cmd.familyName() != null) {
      family.setFamilyName(cmd.familyName());
    }
    if (cmd.memberCount() != null) {
      family.setMemberCount(cmd.memberCount());
    }
    return familyRepo.save(family);
  }

  public void deleteFamily(Long id) {
    if (!familyRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Family not found");
    }
    familyRepo.deleteById(id);
  }

  public List<Family> listFamilies(String familyName) {
    return ListFilterSupport.filter(familyRepo.findAll(),
        ListFilterSupport.containsIgnoreCase(familyName, Family::getFamilyName));
  }

  public Family getFamily(Long id) {
    return requireFamily(id);
  }

  // ── UC: Przypisanie parafianina do rodziny ────────────────────────────────────

  public Parishioner assignParishionerToFamily(Long parishionerId, Long familyId) {
    Parishioner parishioner = requireParishioner(parishionerId);
    Family family = requireFamily(familyId);
    parishioner.setFamily(family);
    return parishionerRepo.save(parishioner);
  }

  // ── UC: Dodanie adresu rodziny ────────────────────────────────────────────────

  public FamilyAddress addFamilyAddress(AddAddressCommand cmd) {
    if (cmd.familyId() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "familyId is required");
    }
    Family family = requireFamily(cmd.familyId());
    if (addressRepo.findByFamily_Id(cmd.familyId()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Family already has an address");
    }
    FamilyAddress address = factory.createFamilyAddress(
        cmd.street(), cmd.houseNumber(), cmd.apartmentNumber(),
        cmd.postalCode(), cmd.city(), family);
    return addressRepo.save(address);
  }

  // ── UC: Zmiana adresu rodziny ─────────────────────────────────────────────────

  public FamilyAddress updateFamilyAddress(Long id, AddAddressCommand cmd) {
    FamilyAddress address = addressRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));
    address.setStreet(cmd.street());
    address.setHouseNumber(cmd.houseNumber());
    address.setApartmentNumber(cmd.apartmentNumber());
    address.setPostalCode(cmd.postalCode());
    address.setCity(cmd.city());
    return addressRepo.save(address);
  }

  public FamilyAddress patchFamilyAddress(Long id, PatchAddressCommand cmd) {
    FamilyAddress address = addressRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));
    if (cmd.street() != null) {
      address.setStreet(cmd.street());
    }
    if (cmd.houseNumber() != null) {
      address.setHouseNumber(cmd.houseNumber());
    }
    if (cmd.apartmentNumber() != null) {
      address.setApartmentNumber(cmd.apartmentNumber());
    }
    if (cmd.postalCode() != null) {
      address.setPostalCode(cmd.postalCode());
    }
    if (cmd.city() != null) {
      address.setCity(cmd.city());
    }
    return addressRepo.save(address);
  }

  public void deleteFamilyAddress(Long id) {
    if (!addressRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found");
    }
    addressRepo.deleteById(id);
  }

  public List<FamilyAddress> listFamilyAddresses(Long familyId, String city, String postalCode) {
    return ListFilterSupport.filter(addressRepo.findAll(),
        ListFilterSupport.eqLong(familyId, a -> a.getFamily() != null ? a.getFamily().getId() : null),
        ListFilterSupport.containsIgnoreCase(city, FamilyAddress::getCity),
        ListFilterSupport.eq(postalCode, FamilyAddress::getPostalCode));
  }

  // ── Internal helpers ─────────────────────────────────────────────────────────

  private Family requireFamily(Long id) {
    return familyRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Family not found"));
  }

  private Parishioner requireParishioner(Long id) {
    return parishionerRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parishioner not found"));
  }
}
