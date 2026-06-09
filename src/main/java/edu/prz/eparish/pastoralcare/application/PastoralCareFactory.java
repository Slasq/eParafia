package edu.prz.eparish.pastoralcare.application;

import edu.prz.eparish.api.support.EntityIds;
import edu.prz.eparish.pastoralcare.domain.familyaddress.FamilyAddress;
import edu.prz.eparish.pastoralcare.domain.familyaddress.FamilyAddressRepository;
import edu.prz.eparish.pastoralcare.domain.family.Family;
import edu.prz.eparish.pastoralcare.domain.family.FamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PastoralCareFactory {

  private final FamilyRepository familyRepo;
  private final FamilyAddressRepository addressRepo;

  public Family createFamily(String familyName, Integer memberCount) {
    Family family = new Family();
    family.setId(EntityIds.nextId(familyRepo, Family::getId));
    family.setFamilyName(familyName);
    family.setMemberCount(memberCount);
    return family;
  }

  public FamilyAddress createFamilyAddress(
      String street, String houseNumber, String apartmentNumber,
      String postalCode, String city, Family family) {
    FamilyAddress address = new FamilyAddress();
    address.setId(EntityIds.nextId(addressRepo, FamilyAddress::getId));
    address.setStreet(street);
    address.setHouseNumber(houseNumber);
    address.setApartmentNumber(apartmentNumber);
    address.setPostalCode(postalCode);
    address.setCity(city);
    address.setFamily(family);
    return address;
  }
}
