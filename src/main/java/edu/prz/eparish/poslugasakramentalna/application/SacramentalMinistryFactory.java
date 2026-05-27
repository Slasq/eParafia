package edu.prz.eparish.poslugasakramentalna.application;

import edu.prz.eparish.api.support.EntityIds;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.ParafianinId;
import edu.prz.eparish.informacjeoparafii.domain.parafia.Parafia;
import edu.prz.eparish.poslugasakramentalna.domain.ksiadz.Ksiadz;
import edu.prz.eparish.poslugasakramentalna.domain.ksiadz.KsiadzId;
import edu.prz.eparish.poslugasakramentalna.domain.ksiadz.KsiadzRepozytorium;
import edu.prz.eparish.poslugasakramentalna.domain.sakrament.Sakrament;
import edu.prz.eparish.poslugasakramentalna.domain.sakrament.SakramentId;
import edu.prz.eparish.poslugasakramentalna.domain.sakrament.SakramentRepozytorium;
import edu.prz.eparish.poslugasakramentalna.domain.udzielaniesakramentu.UdzielanieSakramentu;
import edu.prz.eparish.poslugasakramentalna.domain.udzielaniesakramentu.UdzielanieSakramentuRepozytorium;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SacramentalMinistryFactory {

  private final KsiadzRepozytorium priestRepo;
  private final SakramentRepozytorium sacramentRepo;
  private final UdzielanieSakramentuRepozytorium administrationRepo;

  public Ksiadz createPriest(
      String firstName, String lastName, String phone, String email,
      LocalDate ordinationDate, String role, Parafia parish) {
    Ksiadz priest = new Ksiadz();
    priest.setId(EntityIds.nextId(priestRepo, Ksiadz::getId));
    priest.setImie(firstName);
    priest.setNazwisko(lastName);
    priest.setTelefon(phone);
    priest.setEmail(email);
    priest.setDataSwiecen(ordinationDate);
    priest.setFunkcja(role);
    priest.setParafia(parish);
    return priest;
  }

  public Sakrament createSacrament(String name, String description) {
    Sakrament sacrament = new Sakrament();
    sacrament.setId(EntityIds.nextId(sacramentRepo, Sakrament::getId));
    sacrament.setNazwa(name);
    sacrament.setOpis(description);
    return sacrament;
  }

  public UdzielanieSakramentu createAdministration(
      LocalDate date, Long parishionerId, Long priestId, Long sacramentId) {
    UdzielanieSakramentu administration = new UdzielanieSakramentu();
    administration.setId(EntityIds.nextId(administrationRepo, UdzielanieSakramentu::getId));
    administration.setDataUdzielenia(date);
    administration.setParafianinId(new ParafianinId(parishionerId));
    administration.setKsiadzId(new KsiadzId(priestId));
    administration.setSakramentId(new SakramentId(sacramentId));
    return administration;
  }
}
