package edu.prz.eparish.parishgroups.application;

import edu.prz.eparish.api.support.EntityIds;
import edu.prz.eparish.pastoralcare.domain.parishioner.Parishioner;
import edu.prz.eparish.parishgroups.domain.membership.Membership;
import edu.prz.eparish.parishgroups.domain.membership.MembershipRepository;
import edu.prz.eparish.parishgroups.domain.group.ParishGroup;
import edu.prz.eparish.parishgroups.domain.group.ParishGroupRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParishGroupFactory {

  private final ParishGroupRepository groupRepo;
  private final MembershipRepository membershipRepo;

  public ParishGroup createGroup(String name, String description, String supervisor) {
    ParishGroup group = new ParishGroup();
    group.setId(EntityIds.nextId(groupRepo, ParishGroup::getId));
    group.setName(name);
    group.setDescription(description);
    group.setSupervisor(supervisor);
    return group;
  }

  public Membership createMembership(
      ParishGroup group, Parishioner parishioner,
      LocalDate startDate, LocalDate endDate) {
    Membership membership = new Membership();
    membership.setId(EntityIds.nextId(membershipRepo, Membership::getId));
    membership.setGroup(group);
    membership.setParishioner(parishioner);
    membership.setStartDate(startDate);
    membership.setEndDate(endDate);
    return membership;
  }
}
