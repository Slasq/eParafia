package edu.prz.eparish.parishgroups.application;

import edu.prz.eparish.pastoralcare.domain.parishioner.Parishioner;
import edu.prz.eparish.pastoralcare.domain.parishioner.ParishionerRepository;
import edu.prz.eparish.parishgroups.domain.membership.Membership;
import edu.prz.eparish.parishgroups.domain.membership.MembershipRepository;
import edu.prz.eparish.parishgroups.domain.group.ParishGroup;
import edu.prz.eparish.parishgroups.domain.group.ParishGroupAggregate;
import edu.prz.eparish.parishgroups.domain.group.ParishGroupRepository;
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
  private final ParishGroupRepository groupRepo;
  private final MembershipRepository membershipRepo;
  private final ParishionerRepository parishionerRepo;

  // ── Commands ────────────────────────────────────────────────────────────────

  public record CreateGroupCommand(String name, String description, String supervisor) {}

  public record AddMembershipCommand(
      Long groupId, Long parishionerId, LocalDate startDate, LocalDate endDate) {}

  public record PatchGroupCommand(String name, String description, String supervisor) {}

  public record PatchMembershipCommand(LocalDate startDate, LocalDate endDate) {}

  // ── UC: Dodaj grupę parafialną ───────────────────────────────────────────────

  public ParishGroup createGroup(CreateGroupCommand cmd) {
    ParishGroup group = factory.createGroup(cmd.name(), cmd.description(), cmd.supervisor());
    return groupRepo.save(group);
  }

  // ── UC: Zmień grupę parafialną ────────────────────────────────────────────────

  public ParishGroup updateGroup(Long id, CreateGroupCommand cmd) {
    ParishGroup group = requireGroup(id);
    group.setName(cmd.name());
    group.setDescription(cmd.description());
    group.setSupervisor(cmd.supervisor());
    return groupRepo.save(group);
  }

  public ParishGroup patchGroup(Long id, PatchGroupCommand cmd) {
    ParishGroup group = requireGroup(id);
    if (cmd.name() != null) {
      group.setName(cmd.name());
    }
    if (cmd.description() != null) {
      group.setDescription(cmd.description());
    }
    if (cmd.supervisor() != null) {
      group.setSupervisor(cmd.supervisor());
    }
    return groupRepo.save(group);
  }

  public void deleteGroup(Long id) {
    if (!groupRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
    }
    groupRepo.deleteById(id);
  }

  public List<ParishGroup> listGroups(String name, String supervisor) {
    return ListFilterSupport.filter(groupRepo.findAll(),
        ListFilterSupport.containsIgnoreCase(name, ParishGroup::getName),
        ListFilterSupport.containsIgnoreCase(supervisor, ParishGroup::getSupervisor));
  }

  public ParishGroup getGroup(Long id) {
    return requireGroup(id);
  }

  // ── UC: Dodaj członkostwo (z opcjonalnymi datami) ────────────────────────────

  public Membership addMembership(AddMembershipCommand cmd) {
    ParishGroup group = requireGroup(cmd.groupId());
    Parishioner parishioner = parishionerRepo.findById(cmd.parishionerId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parishioner not found"));
    Membership membership = factory.createMembership(
        group, parishioner, cmd.startDate(), cmd.endDate());
    return membershipRepo.save(membership);
  }

  // ── UC: Zakończ członkostwo (dodaj datę zakończenia) ─────────────────────────

  public Membership terminateMembership(Long membershipId, LocalDate endDate) {
    Membership membership = membershipRepo.findById(membershipId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership not found"));
    membership.setEndDate(endDate);
    return membershipRepo.save(membership);
  }

  public Membership patchMembership(Long membershipId, PatchMembershipCommand cmd) {
    Membership membership = membershipRepo.findById(membershipId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership not found"));
    if (cmd.startDate() != null) {
      membership.setStartDate(cmd.startDate());
    }
    if (cmd.endDate() != null) {
      membership.setEndDate(cmd.endDate());
    }
    return membershipRepo.save(membership);
  }

  public void deleteMembership(Long membershipId) {
    if (!membershipRepo.existsById(membershipId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership not found");
    }
    membershipRepo.deleteById(membershipId);
  }

  public List<Membership> listMemberships(Long groupId, Long parishionerId) {
    List<Membership> source;
    if (groupId != null) {
      source = membershipRepo.findByGroup_Id(groupId);
    } else if (parishionerId != null) {
      source = membershipRepo.findByParishioner_Id(parishionerId);
    } else {
      source = membershipRepo.findAll();
    }
    return source;
  }

  // ── Agregat ParishGroupAggregate ─────────────────────────────────────────────

  public ParishGroupAggregate getGroupAggregate(Long id) {
    ParishGroup root = requireGroup(id);
    List<Membership> memberships = membershipRepo.findByGroup_Id(id);
    return new ParishGroupAggregate(root, memberships);
  }

  // ── Internal helpers ─────────────────────────────────────────────────────────

  private ParishGroup requireGroup(Long id) {
    return groupRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
  }
}
