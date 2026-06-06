package edu.prz.eparish.duszpasterstwowiernych.application;

import edu.prz.eparish.duszpasterstwowiernych.domain.adresrodziny.AdresRodziny;
import edu.prz.eparish.duszpasterstwowiernych.domain.adresrodziny.AdresRodzinyRepozytorium;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.Parafianin;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.ParafianinRepozytorium;
import edu.prz.eparish.duszpasterstwowiernych.domain.rodzina.Rodzina;
import edu.prz.eparish.duszpasterstwowiernych.domain.rodzina.RodzinaRepozytorium;
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

  public List<Rodzina> listFamilies() {
    return familyRepo.findAll();
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
    Rodzina family = requireFamily(cmd.familyId());
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

  public List<AdresRodziny> listFamilyAddresses() {
    return addressRepo.findAll();
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
