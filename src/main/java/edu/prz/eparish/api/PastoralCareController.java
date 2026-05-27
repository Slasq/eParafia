package edu.prz.eparish.api;

import edu.prz.eparish.duszpasterstwowiernych.application.PastoralCareService;
import edu.prz.eparish.duszpasterstwowiernych.application.PastoralCareService.AddAddressCommand;
import edu.prz.eparish.duszpasterstwowiernych.application.PastoralCareService.AddFamilyCommand;
import edu.prz.eparish.duszpasterstwowiernych.domain.adresrodziny.AdresRodziny;
import edu.prz.eparish.duszpasterstwowiernych.domain.rodzina.Rodzina;
import edu.prz.eparish.grupyparafialne.application.ParishGroupService;
import edu.prz.eparish.grupyparafialne.application.ParishGroupService.AddMembershipCommand;
import edu.prz.eparish.grupyparafialne.application.ParishGroupService.CreateGroupCommand;
import edu.prz.eparish.grupyparafialne.domain.czlonkostwo.Czlonkostwo;
import edu.prz.eparish.grupyparafialne.domain.grupa.GrupaParafialna;
import edu.prz.eparish.grupyparafialne.domain.grupa.GrupaParafialnaAgregat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Pastoral Care & Groups", description = "Families, groups, memberships — GrupaParafialnaAgregat")
public class PastoralCareController {

  private final PastoralCareService pastoralCareService;
  private final ParishGroupService groupService;

  // ── FAMILIES — UC: Zarządzanie wspólnotą parafialną ─────────────────────────

  @GetMapping("/families")
  @Operation(summary = "List all families")
  public List<FamilyResponse> listFamilies() {
    return pastoralCareService.listFamilies().stream().map(this::toFamilyResponse).toList();
  }

  @GetMapping("/families/{id}")
  @Operation(summary = "Get family by ID")
  public FamilyResponse getFamily(@PathVariable Long id) {
    return toFamilyResponse(pastoralCareService.getFamily(id));
  }

  @PostMapping("/families")
  @Operation(summary = "UC: Zarządzanie wspólnotą — add family")
  public ResponseEntity<FamilyResponse> addFamily(@RequestBody AddFamilyRequest req) {
    Rodzina family = pastoralCareService.addFamily(new AddFamilyCommand(req.familyName(), req.memberCount()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toFamilyResponse(family));
  }

  @PutMapping("/families/{id}/name")
  @Operation(summary = "UC: Zmiana nazwiska rodziny — update family name")
  public FamilyResponse updateFamilyName(@PathVariable Long id, @RequestBody UpdateFamilyNameRequest req) {
    return toFamilyResponse(pastoralCareService.updateFamilyName(id, req.familyName()));
  }

  @PutMapping("/families/{id}/member-count")
  @Operation(summary = "UC: Dodaj liczbę członków rodziny — update member count")
  public FamilyResponse updateMemberCount(@PathVariable Long id, @RequestBody UpdateMemberCountRequest req) {
    return toFamilyResponse(pastoralCareService.updateMemberCount(id, req.memberCount()));
  }

  // ── ASSIGN PARISHIONER TO FAMILY — UC: Przypisanie do rodziny ───────────────

  @PutMapping("/parishioners/{parishionerId}/family/{familyId}")
  @Operation(summary = "UC: Przypisanie do rodziny — assign parishioner to family")
  public AssignmentResponse assignToFamily(
      @PathVariable Long parishionerId, @PathVariable Long familyId) {
    pastoralCareService.assignParishionerToFamily(parishionerId, familyId);
    return new AssignmentResponse(parishionerId, familyId);
  }

  // ── FAMILY ADDRESSES — UC: Dodanie / zmiana adresu ──────────────────────────

  @GetMapping("/family-addresses")
  @Operation(summary = "List all family addresses")
  public List<FamilyAddressResponse> listFamilyAddresses() {
    return pastoralCareService.listFamilyAddresses().stream().map(this::toAddressResponse).toList();
  }

  @PostMapping("/family-addresses")
  @Operation(summary = "UC: Dodanie adresu — add family address")
  public ResponseEntity<FamilyAddressResponse> addFamilyAddress(@RequestBody AddFamilyAddressRequest req) {
    AdresRodziny address = pastoralCareService.addFamilyAddress(new AddAddressCommand(
        req.street(), req.houseNumber(), req.apartmentNumber(),
        req.postalCode(), req.city(), req.familyId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toAddressResponse(address));
  }

  @PutMapping("/family-addresses/{id}")
  @Operation(summary = "UC: Zmiana adresu — update family address")
  public FamilyAddressResponse updateFamilyAddress(
      @PathVariable Long id, @RequestBody AddFamilyAddressRequest req) {
    AdresRodziny address = pastoralCareService.updateFamilyAddress(id, new AddAddressCommand(
        req.street(), req.houseNumber(), req.apartmentNumber(),
        req.postalCode(), req.city(), req.familyId()));
    return toAddressResponse(address);
  }

  // ── GROUPS — UC: Dodaj / zmień grupę parafialną ──────────────────────────────

  @GetMapping("/groups")
  @Operation(summary = "List all parish groups")
  public List<GroupResponse> listGroups() {
    return groupService.listGroups().stream().map(this::toGroupResponse).toList();
  }

  @GetMapping("/groups/{id}")
  @Operation(summary = "Get group by ID")
  public GroupResponse getGroup(@PathVariable Long id) {
    return toGroupResponse(groupService.getGroup(id));
  }

  @PostMapping("/groups")
  @Operation(summary = "UC: Dodaj grupę — create parish group")
  public ResponseEntity<GroupResponse> createGroup(@RequestBody CreateGroupRequest req) {
    GrupaParafialna group = groupService.createGroup(
        new CreateGroupCommand(req.name(), req.description(), req.supervisor()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toGroupResponse(group));
  }

  @PutMapping("/groups/{id}")
  @Operation(summary = "UC: Zmień grupę — update parish group")
  public GroupResponse updateGroup(@PathVariable Long id, @RequestBody CreateGroupRequest req) {
    return toGroupResponse(groupService.updateGroup(id,
        new CreateGroupCommand(req.name(), req.description(), req.supervisor())));
  }

  // ── GROUP AGGREGATE — GrupaParafialnaAgregat ─────────────────────────────────

  @GetMapping("/groups/{id}/aggregate")
  @Operation(summary = "UC: GrupaParafialnaAgregat — group with memberships",
      description = "Returns group with all memberships. Domain methods: activeMembers(), "
          + "formerMembers(), isMember(), activeMembersAt(date).")
  public GroupAggregateResponse getGroupAggregate(@PathVariable Long id) {
    GrupaParafialnaAgregat agg = groupService.getGroupAggregate(id);
    return toGroupAggregateResponse(agg);
  }

  // ── MEMBERSHIPS — UC: Dodaj członkostwo ─────────────────────────────────────

  @GetMapping("/memberships")
  @Operation(summary = "List all memberships")
  public List<MembershipResponse> listMemberships() {
    return groupService.listMemberships().stream().map(this::toMembershipResponse).toList();
  }

  @PostMapping("/memberships")
  @Operation(summary = "UC: Dodaj członkostwo — add membership (with optional dates)")
  public ResponseEntity<MembershipResponse> addMembership(@RequestBody AddMembershipRequest req) {
    Czlonkostwo membership = groupService.addMembership(
        new AddMembershipCommand(req.groupId(), req.parishionerId(), req.startDate(), req.endDate()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toMembershipResponse(membership));
  }

  @PutMapping("/memberships/{id}/terminate")
  @Operation(summary = "UC: Zakończ członkostwo — set membership end date")
  public MembershipResponse terminateMembership(
      @PathVariable Long id, @RequestBody TerminateMembershipRequest req) {
    return toMembershipResponse(groupService.terminateMembership(id, req.endDate()));
  }

  // ── Mapping helpers ──────────────────────────────────────────────────────────

  private FamilyResponse toFamilyResponse(Rodzina r) {
    return new FamilyResponse(r.getId(), r.getNazwiskoRodziny(), r.getLiczbaCzlonkow());
  }

  private FamilyAddressResponse toAddressResponse(AdresRodziny a) {
    return new FamilyAddressResponse(a.getId(), a.getUlica(), a.getNumerDomu(),
        a.getNumerMieszkania(), a.getKodPocztowy(), a.getMiasto(), a.getRodzina().getId());
  }

  private GroupResponse toGroupResponse(GrupaParafialna g) {
    return new GroupResponse(g.getId(), g.getNazwa(), g.getOpis(), g.getOpiekun());
  }

  private MembershipResponse toMembershipResponse(Czlonkostwo c) {
    return new MembershipResponse(c.getId(), c.getDataOdKiedy(), c.getDataDoKiedy(),
        c.getGrupa().getId(), c.getParafianin().getId());
  }

  private GroupAggregateResponse toGroupAggregateResponse(GrupaParafialnaAgregat agg) {
    return new GroupAggregateResponse(
        toGroupResponse(agg.getRoot()),
        agg.totalMemberCount(),
        agg.activeMembers().size(),
        agg.formerMembers().size(),
        agg.getCzlonkostwa().stream().map(this::toMembershipResponse).toList());
  }

  // ── Request / Response records ───────────────────────────────────────────────

  public record AddFamilyRequest(String familyName, Integer memberCount) {}
  public record UpdateFamilyNameRequest(String familyName) {}
  public record UpdateMemberCountRequest(Integer memberCount) {}
  public record FamilyResponse(Long id, String familyName, Integer memberCount) {}
  public record AssignmentResponse(Long parishionerId, Long familyId) {}

  public record AddFamilyAddressRequest(String street, String houseNumber, String apartmentNumber,
      String postalCode, String city, Long familyId) {}
  public record FamilyAddressResponse(Long id, String street, String houseNumber,
      String apartmentNumber, String postalCode, String city, Long familyId) {}

  public record CreateGroupRequest(String name, String description, String supervisor) {}
  public record GroupResponse(Long id, String name, String description, String supervisor) {}

  public record GroupAggregateResponse(GroupResponse group, int totalMembers,
      int activeMembers, int formerMembers, List<MembershipResponse> memberships) {}

  public record AddMembershipRequest(Long groupId, Long parishionerId,
      LocalDate startDate, LocalDate endDate) {}
  public record TerminateMembershipRequest(LocalDate endDate) {}
  public record MembershipResponse(Long id, LocalDate startDate, LocalDate endDate,
      Long groupId, Long parishionerId) {}
}
