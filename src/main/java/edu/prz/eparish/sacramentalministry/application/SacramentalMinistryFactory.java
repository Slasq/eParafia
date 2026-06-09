package edu.prz.eparish.sacramentalministry.application;

import edu.prz.eparish.api.support.EntityIds;
import edu.prz.eparish.pastoralcare.domain.parishioner.ParishionerId;
import edu.prz.eparish.parishinformation.domain.parish.Parish;
import edu.prz.eparish.sacramentalministry.domain.priest.Priest;
import edu.prz.eparish.sacramentalministry.domain.priest.PriestId;
import edu.prz.eparish.sacramentalministry.domain.priest.PriestRepository;
import edu.prz.eparish.sacramentalministry.domain.sacrament.Sacrament;
import edu.prz.eparish.sacramentalministry.domain.sacrament.SacramentId;
import edu.prz.eparish.sacramentalministry.domain.sacrament.SacramentRepository;
import edu.prz.eparish.sacramentalministry.domain.sacramentadministration.SacramentAdministration;
import edu.prz.eparish.sacramentalministry.domain.sacramentadministration.SacramentAdministrationRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SacramentalMinistryFactory {

  private final PriestRepository priestRepo;
  private final SacramentRepository sacramentRepo;
  private final SacramentAdministrationRepository administrationRepo;

  public Priest createPriest(
      String firstName, String lastName, String phone, String email,
      LocalDate ordinationDate, String role, Parish parish) {
    Priest priest = new Priest();
    priest.setId(EntityIds.nextId(priestRepo, Priest::getId));
    priest.setFirstName(firstName);
    priest.setLastName(lastName);
    priest.setPhone(phone);
    priest.setEmail(email);
    priest.setOrdinationDate(ordinationDate);
    priest.setRole(role);
    priest.setParish(parish);
    return priest;
  }

  public Sacrament createSacrament(String name, String description) {
    Sacrament sacrament = new Sacrament();
    sacrament.setId(EntityIds.nextId(sacramentRepo, Sacrament::getId));
    sacrament.setName(name);
    sacrament.setDescription(description);
    return sacrament;
  }

  public SacramentAdministration createAdministration(
      LocalDate date, Long parishionerId, Long priestId, Long sacramentId) {
    SacramentAdministration administration = new SacramentAdministration();
    administration.setId(EntityIds.nextId(administrationRepo, SacramentAdministration::getId));
    administration.setAdministrationDate(date);
    administration.setParishionerId(new ParishionerId(parishionerId));
    administration.setPriestId(new PriestId(priestId));
    administration.setSacramentId(new SacramentId(sacramentId));
    return administration;
  }
}
