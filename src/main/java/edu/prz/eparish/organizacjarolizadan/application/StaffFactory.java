package edu.prz.eparish.organizacjarolizadan.application;

import edu.prz.eparish.api.support.EntityIds;
import edu.prz.eparish.informacjeoparafii.domain.parafia.Parafia;
import edu.prz.eparish.organizacjarolizadan.domain.obowiazek.Obowiazek;
import edu.prz.eparish.organizacjarolizadan.domain.obowiazek.ObowiazekRepozytorium;
import edu.prz.eparish.organizacjarolizadan.domain.pracownik.Pracownik;
import edu.prz.eparish.organizacjarolizadan.domain.pracownik.PracownikRepozytorium;
import edu.prz.eparish.organizacjarolizadan.domain.stanowisko.Stanowisko;
import edu.prz.eparish.organizacjarolizadan.domain.stanowisko.StanowiskoRepozytorium;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StaffFactory {

  private final PracownikRepozytorium employeeRepo;
  private final StanowiskoRepozytorium positionRepo;
  private final ObowiazekRepozytorium dutyRepo;

  public Pracownik createEmployee(String firstName, String lastName, Parafia parish, Stanowisko position) {
    Pracownik employee = new Pracownik();
    employee.setId(EntityIds.nextId(employeeRepo, Pracownik::getId));
    employee.setImie(firstName);
    employee.setNazwisko(lastName);
    employee.setParafia(parish);
    employee.setStanowisko(position);
    return employee;
  }

  public Stanowisko createPosition(String name, String description) {
    Stanowisko position = new Stanowisko();
    position.setId(EntityIds.nextId(positionRepo, Stanowisko::getId));
    position.setNazwa(name);
    position.setOpis(description);
    return position;
  }

  public Obowiazek createDuty(String name, String description, Stanowisko position) {
    Obowiazek duty = new Obowiazek();
    duty.setId(EntityIds.nextId(dutyRepo, Obowiazek::getId));
    duty.setNazwa(name);
    duty.setOpis(description);
    duty.setStanowisko(position);
    duty.setStatus("ASSIGNED");
    return duty;
  }
}
