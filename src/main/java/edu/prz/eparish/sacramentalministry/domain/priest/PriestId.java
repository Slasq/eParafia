package edu.prz.eparish.sacramentalministry.domain.priest;

import jakarta.persistence.Embeddable;

@Embeddable
public record PriestId(Long value) {
}
