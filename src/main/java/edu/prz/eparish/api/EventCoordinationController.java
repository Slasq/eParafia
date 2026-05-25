package edu.prz.eparish.api;

import edu.prz.eparish.api.support.EntityIds;
import edu.prz.eparish.informacjeoparafii.domain.parafia.Parafia;
import edu.prz.eparish.informacjeoparafii.domain.parafia.ParafiaRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.harmonogram.Harmonogram;
import edu.prz.eparish.koordynacjawydarzen.domain.harmonogram.HarmonogramRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.ofiara.Ofiara;
import edu.prz.eparish.koordynacjawydarzen.domain.ofiara.OfiaraRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.organizator.Organizator;
import edu.prz.eparish.koordynacjawydarzen.domain.organizator.OrganizatorRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.typwydarzenia.TypWydarzenia;
import edu.prz.eparish.koordynacjawydarzen.domain.typwydarzenia.TypWydarzeniaRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.uczestnik.Uczestnik;
import edu.prz.eparish.koordynacjawydarzen.domain.uczestnik.UczestnikRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.wydarzenie.WydarzenieParafialne;
import edu.prz.eparish.koordynacjawydarzen.domain.wydarzenie.WydarzenieParafialneRepozytorium;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
public class EventCoordinationController {

  private final WydarzenieParafialneRepozytorium wydarzenieParafialneRepozytorium;
  private final TypWydarzeniaRepozytorium typWydarzeniaRepozytorium;
  private final HarmonogramRepozytorium harmonogramRepozytorium;
  private final ParafiaRepozytorium parafiaRepozytorium;
  private final OfiaraRepozytorium ofiaraRepozytorium;
  private final UczestnikRepozytorium uczestnikRepozytorium;
  private final OrganizatorRepozytorium organizatorRepozytorium;

  @GetMapping("/events")
  public List<EventResponse> listEvents() {
    return wydarzenieParafialneRepozytorium.findAll().stream().map(this::toEventResponse).toList();
  }

  @GetMapping("/events/{id}")
  public EventResponse getEvent(@PathVariable Long id) {
    return toEventResponse(findEvent(id));
  }

  @PostMapping("/events")
  public ResponseEntity<EventResponse> addEvent(@RequestBody AddEventRequest request) {
    Parafia parafia = parafiaRepozytorium.findById(request.parishId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parafia nie istnieje"));
    TypWydarzenia typ = typWydarzeniaRepozytorium.findById(request.eventTypeId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Typ wydarzenia nie istnieje"));
    Harmonogram harmonogram = harmonogramRepozytorium.findById(request.scheduleId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Harmonogram nie istnieje"));

    WydarzenieParafialne wydarzenie = new WydarzenieParafialne();
    wydarzenie.setId(EntityIds.nextId(wydarzenieParafialneRepozytorium, WydarzenieParafialne::getId));
    wydarzenie.setNazwa(request.name());
    wydarzenie.setDataIGodzina(request.dateTime());
    wydarzenie.setMiejsce(request.place());
    wydarzenie.setOpis(request.description());
    wydarzenie.setParafia(parafia);
    wydarzenie.setTypWydarzenia(typ);
    wydarzenie.setHarmonogram(harmonogram);

    WydarzenieParafialne saved = wydarzenieParafialneRepozytorium.save(wydarzenie);
    return ResponseEntity.status(HttpStatus.CREATED).body(toEventResponse(saved));
  }

  @GetMapping("/event-types")
  public List<EventTypeResponse> listEventTypes() {
    return typWydarzeniaRepozytorium.findAll().stream()
        .map(t -> new EventTypeResponse(t.getId(), t.getNazwa()))
        .toList();
  }

  @GetMapping("/schedules")
  public List<ScheduleResponse> listSchedules() {
    return harmonogramRepozytorium.findAll().stream()
        .map(h -> new ScheduleResponse(h.getId(), h.getData(), h.getGodzina(), h.getOpis()))
        .toList();
  }

  @PostMapping("/schedules")
  public ResponseEntity<ScheduleResponse> addSchedule(@RequestBody AddScheduleRequest request) {
    Harmonogram harmonogram = new Harmonogram();
    harmonogram.setId(EntityIds.nextId(harmonogramRepozytorium, Harmonogram::getId));
    harmonogram.setData(request.date());
    harmonogram.setGodzina(request.time());
    harmonogram.setOpis(request.description());

    Harmonogram saved = harmonogramRepozytorium.save(harmonogram);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ScheduleResponse(saved.getId(), saved.getData(), saved.getGodzina(), saved.getOpis()));
  }

  @GetMapping("/offerings")
  public List<OfferingResponse> listOfferings() {
    return ofiaraRepozytorium.findAll().stream().map(this::toOfferingResponse).toList();
  }

  @PostMapping("/offerings")
  public ResponseEntity<OfferingResponse> addOffering(@RequestBody AddOfferingRequest request) {
    WydarzenieParafialne wydarzenie = findEvent(request.eventId());

    Ofiara ofiara = new Ofiara();
    ofiara.setId(EntityIds.nextId(ofiaraRepozytorium, Ofiara::getId));
    ofiara.setKwota(request.amount());
    ofiara.setData(request.date());
    ofiara.setTyp(request.type());
    ofiara.setWydarzenie(wydarzenie);

    Ofiara saved = ofiaraRepozytorium.save(ofiara);
    return ResponseEntity.status(HttpStatus.CREATED).body(toOfferingResponse(saved));
  }

  @GetMapping("/participants")
  public List<ParticipantResponse> listParticipants() {
    return uczestnikRepozytorium.findAll().stream().map(this::toParticipantResponse).toList();
  }

  @PostMapping("/participants")
  public ResponseEntity<ParticipantResponse> addParticipant(@RequestBody AddParticipantRequest request) {
    WydarzenieParafialne wydarzenie = findEvent(request.eventId());

    Uczestnik uczestnik = new Uczestnik();
    uczestnik.setId(EntityIds.nextId(uczestnikRepozytorium, Uczestnik::getId));
    uczestnik.setImie(request.firstName());
    uczestnik.setNazwisko(request.lastName());
    uczestnik.setRola(request.role());
    uczestnik.setWydarzenie(wydarzenie);

    Uczestnik saved = uczestnikRepozytorium.save(uczestnik);
    return ResponseEntity.status(HttpStatus.CREATED).body(toParticipantResponse(saved));
  }

  @GetMapping("/organizers")
  public List<OrganizerResponse> listOrganizers() {
    return organizatorRepozytorium.findAll().stream().map(this::toOrganizerResponse).toList();
  }

  @PostMapping("/organizers")
  public ResponseEntity<OrganizerResponse> addOrganizer(@RequestBody AddOrganizerRequest request) {
    WydarzenieParafialne wydarzenie = findEvent(request.eventId());

    Organizator organizator = new Organizator();
    organizator.setId(EntityIds.nextId(organizatorRepozytorium, Organizator::getId));
    organizator.setImie(request.firstName());
    organizator.setNazwisko(request.lastName());
    organizator.setRola(request.role());
    organizator.setWydarzenie(wydarzenie);

    Organizator saved = organizatorRepozytorium.save(organizator);
    return ResponseEntity.status(HttpStatus.CREATED).body(toOrganizerResponse(saved));
  }

  private WydarzenieParafialne findEvent(Long id) {
    return wydarzenieParafialneRepozytorium.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wydarzenie nie istnieje"));
  }

  private EventResponse toEventResponse(WydarzenieParafialne w) {
    return new EventResponse(
        w.getId(),
        w.getNazwa(),
        w.getDataIGodzina(),
        w.getMiejsce(),
        w.getOpis(),
        w.getParafia().getId(),
        w.getTypWydarzenia().getId(),
        w.getHarmonogram().getId());
  }

  private OfferingResponse toOfferingResponse(Ofiara o) {
    return new OfferingResponse(
        o.getId(), o.getKwota(), o.getData(), o.getTyp(), o.getWydarzenie().getId());
  }

  private ParticipantResponse toParticipantResponse(Uczestnik u) {
    return new ParticipantResponse(
        u.getId(), u.getImie(), u.getNazwisko(), u.getRola(), u.getWydarzenie().getId());
  }

  private OrganizerResponse toOrganizerResponse(Organizator o) {
    return new OrganizerResponse(
        o.getId(), o.getImie(), o.getNazwisko(), o.getRola(), o.getWydarzenie().getId());
  }

  public record AddEventRequest(
      String name,
      LocalDateTime dateTime,
      String place,
      String description,
      Long parishId,
      Long eventTypeId,
      Long scheduleId) {}

  public record EventResponse(
      Long id,
      String name,
      LocalDateTime dateTime,
      String place,
      String description,
      Long parishId,
      Long eventTypeId,
      Long scheduleId) {}

  public record EventTypeResponse(Long id, String name) {}

  public record AddScheduleRequest(LocalDate date, LocalTime time, String description) {}

  public record ScheduleResponse(Long id, LocalDate date, LocalTime time, String description) {}

  public record AddOfferingRequest(BigDecimal amount, LocalDate date, String type, Long eventId) {}

  public record OfferingResponse(Long id, BigDecimal amount, LocalDate date, String type, Long eventId) {}

  public record AddParticipantRequest(String firstName, String lastName, String role, Long eventId) {}

  public record ParticipantResponse(Long id, String firstName, String lastName, String role, Long eventId) {}

  public record AddOrganizerRequest(String firstName, String lastName, String role, Long eventId) {}

  public record OrganizerResponse(Long id, String firstName, String lastName, String role, Long eventId) {}
}
