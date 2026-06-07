package edu.prz.eparish.duszpasterstwowiernych.application;

import edu.prz.eparish.duszpasterstwowiernych.domain.adresrodziny.AdresRodziny;
import edu.prz.eparish.duszpasterstwowiernych.domain.adresrodziny.AdresRodzinyRepozytorium;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.Parafianin;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.ParafianinRepozytorium;
import edu.prz.eparish.duszpasterstwowiernych.domain.rodzina.Rodzina;
import edu.prz.eparish.duszpasterstwowiernych.domain.rodzina.RodzinaRepozytorium;
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
  private final RodzinaRepozytorium familyRepo;
  private final AdresRodzinyRepozytorium addressRepo;
  private final ParafianinRepozytorium parishionerRepo;

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

  public Rodzina addFamily(AddFamilyCommand cmd) {
    Rodzina family = factory.createFamily(cmd.familyName(), cmd.memberCount());
    return familyRepo.save(family);
  }

  // ── UC: Zmiana nazwiska rodziny ──────────────────────────────────────────────

  public Rodzina updateFamilyName(Long id, String familyName) {
    Rodzina family = requireFamily(id);
    family.setNazwiskoRodziny(familyName);
    return familyRepo.save(family);
  }

  // ── UC: Dodaj liczbę członków rodziny ────────────────────────────────────────

  public Rodzina updateMemberCount(Long id, Integer count) {
    Rodzina family = requireFamily(id);
    family.setLiczbaCzlonkow(count);
    return familyRepo.save(family);
  }

  public Rodzina patchFamily(Long id, PatchFamilyCommand cmd) {
    Rodzina family = requireFamily(id);
    if (cmd.familyName() != null) {
      family.setNazwiskoRodziny(cmd.familyName());
    }
    if (cmd.memberCount() != null) {
      family.setLiczbaCzlonkow(cmd.memberCount());
    }
    return familyRepo.save(family);
  }

  public void deleteFamily(Long id) {
    if (!familyRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Family not found");
    }
    familyRepo.deleteById(id);
  }

  public List<Rodzina> listFamilies(String familyName) {
    return ListFilterSupport.filter(familyRepo.findAll(),
        ListFilterSupport.containsIgnoreCase(familyName, Rodzina::getNazwiskoRodziny));
  }

  public Rodzina getFamily(Long id) {
    return requireFamily(id);
  }

  // ── UC: Przypisanie parafianina do rodziny ────────────────────────────────────

  public Parafianin assignParishionerToFamily(Long parishionerId, Long familyId) {
    Parafianin parishioner = requireParishioner(parishionerId);
    Rodzina family = requireFamily(familyId);
    parishioner.setRodzina(family);
    return parishionerRepo.save(parishioner);
  }

  // ── UC: Dodanie adresu rodziny ────────────────────────────────────────────────

  public AdresRodziny addFamilyAddress(AddAddressCommand cmd) {
    if (cmd.familyId() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "familyId is required");
    }
    Rodzina family = requireFamily(cmd.familyId());
    if (addressRepo.findByRodzina_Id(cmd.familyId()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Family already has an address");
    }
    AdresRodziny address = factory.createFamilyAddress(
        cmd.street(), cmd.houseNumber(), cmd.apartmentNumber(),
        cmd.postalCode(), cmd.city(), family);
    return addressRepo.save(address);
  }

  // ── UC: Zmiana adresu rodziny ─────────────────────────────────────────────────

  public AdresRodziny updateFamilyAddress(Long id, AddAddressCommand cmd) {
    AdresRodziny address = addressRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));
    address.setUlica(cmd.street());
    address.setNumerDomu(cmd.houseNumber());
    address.setNumerMieszkania(cmd.apartmentNumber());
    address.setKodPocztowy(cmd.postalCode());
    address.setMiasto(cmd.city());
    return addressRepo.save(address);
  }

  public AdresRodziny patchFamilyAddress(Long id, PatchAddressCommand cmd) {
    AdresRodziny address = addressRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));
    if (cmd.street() != null) {
      address.setUlica(cmd.street());
    }
    if (cmd.houseNumber() != null) {
      address.setNumerDomu(cmd.houseNumber());
    }
    if (cmd.apartmentNumber() != null) {
      address.setNumerMieszkania(cmd.apartmentNumber());
    }
    if (cmd.postalCode() != null) {
      address.setKodPocztowy(cmd.postalCode());
    }
    if (cmd.city() != null) {
      address.setMiasto(cmd.city());
    }
    return addressRepo.save(address);
  }

  public void deleteFamilyAddress(Long id) {
    if (!addressRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found");
    }
    addressRepo.deleteById(id);
  }

  public List<AdresRodziny> listFamilyAddresses(Long familyId, String city, String postalCode) {
    return ListFilterSupport.filter(addressRepo.findAll(),
        ListFilterSupport.eqLong(familyId, a -> a.getRodzina() != null ? a.getRodzina().getId() : null),
        ListFilterSupport.containsIgnoreCase(city, AdresRodziny::getMiasto),
        ListFilterSupport.eq(postalCode, AdresRodziny::getKodPocztowy));
  }

  // ── Internal helpers ─────────────────────────────────────────────────────────

  private Rodzina requireFamily(Long id) {
    return familyRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Family not found"));
  }

  private Parafianin requireParishioner(Long id) {
    return parishionerRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parishioner not found"));
  }
}
