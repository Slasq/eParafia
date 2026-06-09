package edu.prz.eparish.sacramentalministry.domain.sacrament;

import jakarta.persistence.Embeddable;

@Embeddable
public record SacramentId(Long value) {
}
