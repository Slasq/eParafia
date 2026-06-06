package edu.prz.eparish.grupyparafialne.application;

import edu.prz.eparish.api.support.EntityIds;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.Parafianin;
import edu.prz.eparish.grupyparafialne.domain.czlonkostwo.Czlonkostwo;
import edu.prz.eparish.grupyparafialne.domain.czlonkostwo.CzlonkostwoRepozytorium;
import edu.prz.eparish.grupyparafialne.domain.grupa.GrupaParafialna;
import edu.prz.eparish.grupyparafialne.domain.grupa.GrupaParafialnaRepozytorium;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParishGroupFactory {

  private final GrupaParafialnaRepozytorium groupRepo;
  private final CzlonkostwoRepozytorium membershipRepo;

  public GrupaParafialna createGroup(String name, String description, String supervisor) {
    GrupaParafialna group = new GrupaParafialna();
    group.setId(EntityIds.nextId(groupRepo, GrupaParafialna::getId));
    group.setNazwa(name);
    group.setOpis(description);
    group.setOpiekun(supervisor);
    return group;
  }

  public Czlonkostwo createMembership(
      GrupaParafialna group, Parafianin parishioner,
      LocalDate startDate, LocalDate endDate) {
    Czlonkostwo membership = new Czlonkostwo();
    membership.setId(EntityIds.nextId(membershipRepo, Czlonkostwo::getId));
    membership.setGrupa(group);
    membership.setParafianin(parishioner);
    membership.setDataOdKiedy(startDate);
    membership.setDataDoKiedy(endDate);
    return membership;
  }
}
