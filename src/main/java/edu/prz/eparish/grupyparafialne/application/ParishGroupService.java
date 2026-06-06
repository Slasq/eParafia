package edu.prz.eparish.grupyparafialne.application;

import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.Parafianin;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.ParafianinRepozytorium;
import edu.prz.eparish.grupyparafialne.domain.czlonkostwo.Czlonkostwo;
import edu.prz.eparish.grupyparafialne.domain.czlonkostwo.CzlonkostwoRepozytorium;
import edu.prz.eparish.grupyparafialne.domain.grupa.GrupaParafialna;
import edu.prz.eparish.grupyparafialne.domain.grupa.GrupaParafialnaAgregat;
import edu.prz.eparish.grupyparafialne.domain.grupa.GrupaParafialnaRepozytorium;
import edu.prz.eparish.api.support.ListFilterSupport;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor
public class ParishGroupService {

  private final ParishGroupFactory factory;
  private final GrupaParafialnaRepozytorium groupRepo;
  private final CzlonkostwoRepozytorium membershipRepo;
  private final ParafianinRepozytorium parishionerRepo;

  // ── Commands ────────────────────────────────────────────────────────────────

  public record CreateGroupCommand(String name, String description, String supervisor) {}

  public record AddMembershipCommand(
      Long groupId, Long parishionerId, LocalDate startDate, LocalDate endDate) {}

  public record PatchGroupCommand(String name, String description, String supervisor) {}

  public record PatchMembershipCommand(LocalDate startDate, LocalDate endDate) {}

  // ── UC: Dodaj grupę parafialną ───────────────────────────────────────────────

  public GrupaParafialna createGroup(CreateGroupCommand cmd) {
    GrupaParafialna group = factory.createGroup(cmd.name(), cmd.description(), cmd.supervisor());
    return groupRepo.save(group);
  }

  // ── UC: Zmień grupę parafialną ────────────────────────────────────────────────

  public GrupaParafialna updateGroup(Long id, CreateGroupCommand cmd) {
    GrupaParafialna group = requireGroup(id);
    group.setNazwa(cmd.name());
    group.setOpis(cmd.description());
    group.setOpiekun(cmd.supervisor());
    return groupRepo.save(group);
  }

  public GrupaParafialna patchGroup(Long id, PatchGroupCommand cmd) {
    GrupaParafialna group = requireGroup(id);
    if (cmd.name() != null) {
      group.setNazwa(cmd.name());
    }
    if (cmd.description() != null) {
      group.setOpis(cmd.description());
    }
    if (cmd.supervisor() != null) {
      group.setOpiekun(cmd.supervisor());
    }
    return groupRepo.save(group);
  }

  public void deleteGroup(Long id) {
    if (!groupRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
    }
    groupRepo.deleteById(id);
  }

  public List<GrupaParafialna> listGroups(String name, String supervisor) {
    return ListFilterSupport.filter(groupRepo.findAll(),
        ListFilterSupport.containsIgnoreCase(name, GrupaParafialna::getNazwa),
        ListFilterSupport.containsIgnoreCase(supervisor, GrupaParafialna::getOpiekun));
  }

  public GrupaParafialna getGroup(Long id) {
    return requireGroup(id);
  }

  // ── UC: Dodaj członkostwo (z opcjonalnymi datami) ────────────────────────────

  public Czlonkostwo addMembership(AddMembershipCommand cmd) {
    GrupaParafialna group = requireGroup(cmd.groupId());
    Parafianin parishioner = parishionerRepo.findById(cmd.parishionerId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parishioner not found"));
    Czlonkostwo membership = factory.createMembership(
        group, parishioner, cmd.startDate(), cmd.endDate());
    return membershipRepo.save(membership);
  }

  // ── UC: Zakończ członkostwo (dodaj datę zakończenia) ─────────────────────────

  public Czlonkostwo terminateMembership(Long membershipId, LocalDate endDate) {
    Czlonkostwo membership = membershipRepo.findById(membershipId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership not found"));
    membership.setDataDoKiedy(endDate);
    return membershipRepo.save(membership);
  }

  public Czlonkostwo patchMembership(Long membershipId, PatchMembershipCommand cmd) {
    Czlonkostwo membership = membershipRepo.findById(membershipId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership not found"));
    if (cmd.startDate() != null) {
      membership.setDataOdKiedy(cmd.startDate());
    }
    if (cmd.endDate() != null) {
      membership.setDataDoKiedy(cmd.endDate());
    }
    return membershipRepo.save(membership);
  }

  public void deleteMembership(Long membershipId) {
    if (!membershipRepo.existsById(membershipId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership not found");
    }
    membershipRepo.deleteById(membershipId);
  }

  public List<Czlonkostwo> listMemberships(Long groupId, Long parishionerId) {
    List<Czlonkostwo> source;
    if (groupId != null) {
      source = membershipRepo.findByGrupa_Id(groupId);
    } else if (parishionerId != null) {
      source = membershipRepo.findByParafianin_Id(parishionerId);
    } else {
      source = membershipRepo.findAll();
    }
    return source;
  }

  // ── Agregat GrupaParafialnaAgregat ────────────────────────────────────────────

  public GrupaParafialnaAgregat getGroupAggregate(Long id) {
    GrupaParafialna root = requireGroup(id);
    List<Czlonkostwo> memberships = membershipRepo.findByGrupa_Id(id);
    return new GrupaParafialnaAgregat(root, memberships);
  }

  // ── Internal helpers ─────────────────────────────────────────────────────────

  private GrupaParafialna requireGroup(Long id) {
    return groupRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
  }
}
