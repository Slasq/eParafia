package edu.prz.eparish.duszpasterstwowiernych.application;

import edu.prz.eparish.api.support.EntityIds;
import edu.prz.eparish.duszpasterstwowiernych.domain.adresrodziny.AdresRodziny;
import edu.prz.eparish.duszpasterstwowiernych.domain.adresrodziny.AdresRodzinyRepozytorium;
import edu.prz.eparish.duszpasterstwowiernych.domain.rodzina.Rodzina;
import edu.prz.eparish.duszpasterstwowiernych.domain.rodzina.RodzinaRepozytorium;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PastoralCareFactory {

  private final RodzinaRepozytorium familyRepo;
  private final AdresRodzinyRepozytorium addressRepo;

  public Rodzina createFamily(String familyName, Integer memberCount) {
    Rodzina family = new Rodzina();
    family.setId(EntityIds.nextId(familyRepo, Rodzina::getId));
    family.setNazwiskoRodziny(familyName);
    family.setLiczbaCzlonkow(memberCount);
    return family;
  }

  public AdresRodziny createFamilyAddress(
      String street, String houseNumber, String apartmentNumber,
      String postalCode, String city, Rodzina family) {
    AdresRodziny address = new AdresRodziny();
    address.setId(EntityIds.nextId(addressRepo, AdresRodziny::getId));
    address.setUlica(street);
    address.setNumerDomu(houseNumber);
    address.setNumerMieszkania(apartmentNumber);
    address.setKodPocztowy(postalCode);
    address.setMiasto(city);
    address.setRodzina(family);
    return address;
  }
}
