package edu.prz.eparish.pastoralcare.domain.familyaddress;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyAddressRepository extends JpaRepository<FamilyAddress, Long> {

  Optional<FamilyAddress> findByFamily_Id(Long familyId);
}
