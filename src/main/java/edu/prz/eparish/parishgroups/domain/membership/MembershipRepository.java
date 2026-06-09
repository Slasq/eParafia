package edu.prz.eparish.parishgroups.domain.membership;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

  List<Membership> findByGroup_Id(Long groupId);

  List<Membership> findByParishioner_Id(Long parishionerId);
}
