package edu.prz.eparish.api;

import edu.prz.eparish.duszpasterstwowiernych.domain.rodzina.Rodzina;
import edu.prz.eparish.duszpasterstwowiernych.domain.rodzina.RodzinaRepozytorium;
import edu.prz.eparish.grupyparafialne.domain.grupa.GrupaParafialna;
import edu.prz.eparish.grupyparafialne.domain.grupa.GrupaParafialnaRepozytorium;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PastoralCareController {

  private final RodzinaRepozytorium rodzinaRepozytorium;
  private final GrupaParafialnaRepozytorium grupaParafialnaRepozytorium;

  @PostMapping("/families")
  public ResponseEntity<FamilyResponse> addFamily(@RequestBody AddFamilyRequest request) {
    Rodzina rodzina = new Rodzina();
    rodzina.setId(nextId(rodzinaRepozytorium));
    rodzina.setNazwiskoRodziny(request.familyName());
    rodzina.setLiczbaCzlonkow(request.memberCount());

    Rodzina saved = rodzinaRepozytorium.save(rodzina);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new FamilyResponse(saved.getId(), saved.getNazwiskoRodziny(), saved.getLiczbaCzlonkow()));
  }

  @PostMapping("/groups")
  public ResponseEntity<GroupResponse> createGroup(@RequestBody CreateGroupRequest request) {
    GrupaParafialna grupa = new GrupaParafialna();
    grupa.setId(nextId(grupaParafialnaRepozytorium));
    grupa.setNazwa(request.name());
    grupa.setOpis(request.description());
    grupa.setOpiekun(request.supervisor());

    GrupaParafialna saved = grupaParafialnaRepozytorium.save(grupa);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new GroupResponse(saved.getId(), saved.getNazwa(), saved.getOpis(), saved.getOpiekun()));
  }

  private Long nextId(RodzinaRepozytorium repository) {
    return repository.findAll().stream()
        .map(Rodzina::getId)
        .max(Long::compareTo)
        .orElse(0L) + 1;
  }

  private Long nextId(GrupaParafialnaRepozytorium repository) {
    return repository.findAll().stream()
        .map(GrupaParafialna::getId)
        .max(Long::compareTo)
        .orElse(0L) + 1;
  }

  public record AddFamilyRequest(String familyName, Integer memberCount) {}

  public record FamilyResponse(Long id, String familyName, Integer memberCount) {}

  public record CreateGroupRequest(String name, String description, String supervisor) {}

  public record GroupResponse(Long id, String name, String description, String supervisor) {}
}
