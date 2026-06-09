package edu.prz.eparish.pastoralcare.domain.parishioner;

import jakarta.persistence.Embeddable;

@Embeddable
public record ParishionerId(Long value) {
}
