package edu.prz.eparish.api;

import edu.prz.eparish.api.support.EntityIds;
import edu.prz.eparish.duszpasterstwowiernych.domain.adresrodziny.AdresRodziny;
import edu.prz.eparish.duszpasterstwowiernych.domain.adresrodziny.AdresRodzinyRepozytorium;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.Parafianin;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.ParafianinRepozytorium;
import edu.prz.eparish.duszpasterstwowiernych.domain.rodzina.Rodzina;
import edu.prz.eparish.duszpasterstwowiernych.domain.rodzina.RodzinaRepozytorium;
import edu.prz.eparish.grupyparafialne.domain.czlonkostwo.Czlonkostwo;
import edu.prz.eparish.grupyparafialne.domain.czlonkostwo.CzlonkostwoRepozytorium;
import edu.prz.eparish.grupyparafialne.domain.grupa.GrupaParafialna;
import edu.prz.eparish.grupyparafialne.domain.grupa.GrupaParafialnaRepozytorium;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PastoralCareController {

  private final RodzinaRepozytorium rodzinaRepozytorium;
  private final GrupaParafialnaRepozytorium grupaParafialnaRepozytorium;
  private final CzlonkostwoRepozytorium czlonkostwoRepozytorium;
  private final AdresRodzinyRepozytorium adresRodzinyRepozytorium;
  private final ParafianinRepozytorium parafianinRepozytorium;

  @GetMapping("/families")
  public List<FamilyResponse> listFamilies() {
    return rodzinaRepozytorium.findAll().stream()
        .map(r -> new FamilyResponse(r.getId(), r.getNazwiskoRodziny(), r.getLiczbaCzlonkow()))
        .toList();
  }

  @GetMapping("/families/{id}")
  public FamilyResponse getFamily(@PathVariable Long id) {
    Rodzina rodzina = rodzinaRepozytorium.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rodzina nie istnieje"));
    return new FamilyResponse(rodzina.getId(), rodzina.getNazwiskoRodziny(), rodzina.getLiczbaCzlonkow());
  }

  @PostMapping("/families")
  public ResponseEntity<FamilyResponse> addFamily(@RequestBody AddFamilyRequest request) {
    Rodzina rodzina = new Rodzina();
    rodzina.setId(EntityIds.nextId(rodzinaRepozytorium, Rodzina::getId));
    rodzina.setNazwiskoRodziny(request.familyName());
    rodzina.setLiczbaCzlonkow(request.memberCount());

    Rodzina saved = rodzinaRepozytorium.save(rodzina);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new FamilyResponse(saved.getId(), saved.getNazwiskoRodziny(), saved.getLiczbaCzlonkow()));
  }

  @GetMapping("/groups")
  public List<GroupResponse> listGroups() {
    return grupaParafialnaRepozytorium.findAll().stream().map(this::toGroupResponse).toList();
  }

  @GetMapping("/groups/{id}")
  public GroupResponse getGroup(@PathVariable Long id) {
    return toGroupResponse(findGroup(id));
  }

  @PostMapping("/groups")
  public ResponseEntity<GroupResponse> createGroup(@RequestBody CreateGroupRequest request) {
    GrupaParafialna grupa = new GrupaParafialna();
    grupa.setId(EntityIds.nextId(grupaParafialnaRepozytorium, GrupaParafialna::getId));
    grupa.setNazwa(request.name());
    grupa.setOpis(request.description());
    grupa.setOpiekun(request.supervisor());

    GrupaParafialna saved = grupaParafialnaRepozytorium.save(grupa);
    return ResponseEntity.status(HttpStatus.CREATED).body(toGroupResponse(saved));
  }

  @GetMapping("/memberships")
  public List<MembershipResponse> listMemberships() {
    return czlonkostwoRepozytorium.findAll().stream().map(this::toMembershipResponse).toList();
  }

  @PostMapping("/memberships")
  public ResponseEntity<MembershipResponse> addMembership(@RequestBody AddMembershipRequest request) {
    GrupaParafialna grupa = findGroup(request.groupId());
    Parafianin parafianin = parafianinRepozytorium.findById(request.parishionerId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parafianin nie istnieje"));

    Czlonkostwo czlonkostwo = new Czlonkostwo();
    czlonkostwo.setId(EntityIds.nextId(czlonkostwoRepozytorium, Czlonkostwo::getId));
    czlonkostwo.setDataOdKiedy(request.startDate());
    czlonkostwo.setDataDoKiedy(request.endDate());
    czlonkostwo.setGrupa(grupa);
    czlonkostwo.setParafianin(parafianin);

    Czlonkostwo saved = czlonkostwoRepozytorium.save(czlonkostwo);
    return ResponseEntity.status(HttpStatus.CREATED).body(toMembershipResponse(saved));
  }

  @GetMapping("/family-addresses")
  public List<FamilyAddressResponse> listFamilyAddresses() {
    return adresRodzinyRepozytorium.findAll().stream().map(this::toFamilyAddressResponse).toList();
  }

  @PostMapping("/family-addresses")
  public ResponseEntity<FamilyAddressResponse> addFamilyAddress(@RequestBody AddFamilyAddressRequest request) {
    Rodzina rodzina = rodzinaRepozytorium.findById(request.familyId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rodzina nie istnieje"));

    AdresRodziny adres = new AdresRodziny();
    adres.setId(EntityIds.nextId(adresRodzinyRepozytorium, AdresRodziny::getId));
    adres.setUlica(request.street());
    adres.setNumerDomu(request.houseNumber());
    adres.setNumerMieszkania(request.apartmentNumber());
    adres.setKodPocztowy(request.postalCode());
    adres.setMiasto(request.city());
    adres.setRodzina(rodzina);

    AdresRodziny saved = adresRodzinyRepozytorium.save(adres);
    return ResponseEntity.status(HttpStatus.CREATED).body(toFamilyAddressResponse(saved));
  }

  private GrupaParafialna findGroup(Long id) {
    return grupaParafialnaRepozytorium.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupa nie istnieje"));
  }

  private GroupResponse toGroupResponse(GrupaParafialna grupa) {
    return new GroupResponse(grupa.getId(), grupa.getNazwa(), grupa.getOpis(), grupa.getOpiekun());
  }

  private MembershipResponse toMembershipResponse(Czlonkostwo c) {
    return new MembershipResponse(
        c.getId(),
        c.getDataOdKiedy(),
        c.getDataDoKiedy(),
        c.getGrupa().getId(),
        c.getParafianin().getId());
  }

  private FamilyAddressResponse toFamilyAddressResponse(AdresRodziny a) {
    return new FamilyAddressResponse(
        a.getId(),
        a.getUlica(),
        a.getNumerDomu(),
        a.getNumerMieszkania(),
        a.getKodPocztowy(),
        a.getMiasto(),
        a.getRodzina().getId());
  }

  public record AddFamilyRequest(String familyName, Integer memberCount) {}

  public record FamilyResponse(Long id, String familyName, Integer memberCount) {}

  public record CreateGroupRequest(String name, String description, String supervisor) {}

  public record GroupResponse(Long id, String name, String description, String supervisor) {}

  public record AddMembershipRequest(
      LocalDate startDate, LocalDate endDate, Long groupId, Long parishionerId) {}

  public record MembershipResponse(
      Long id, LocalDate startDate, LocalDate endDate, Long groupId, Long parishionerId) {}

  public record AddFamilyAddressRequest(
      String street,
      String houseNumber,
      String apartmentNumber,
      String postalCode,
      String city,
      Long familyId) {}

  public record FamilyAddressResponse(
      Long id,
      String street,
      String houseNumber,
      String apartmentNumber,
      String postalCode,
      String city,
      Long familyId) {}
}
