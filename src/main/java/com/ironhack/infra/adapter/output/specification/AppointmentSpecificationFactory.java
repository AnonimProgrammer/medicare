package com.ironhack.infra.adapter.output.specification;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.ironhack.application.dto.query.AppointmentQueryCriteria;
import com.ironhack.domain.AppointmentEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

@Component
public class AppointmentSpecificationFactory {
    private static final ZoneId ZONE = ZoneId.systemDefault();

    public Specification<AppointmentEntity> forFilteredList(
            Optional<UUID> doctorId, Optional<UUID> patientId, AppointmentQueryCriteria criteria) {
        return (root, query, cb) -> {
            if (!Long.class.equals(query.getResultType())) {
                root.fetch("patient", JoinType.INNER);
                root.fetch("doctor", JoinType.INNER);
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();

            doctorId.ifPresent(id -> predicates.add(cb.equal(root.get("doctor").get("id"), id)));
            patientId.ifPresent(
                    id -> predicates.add(cb.equal(root.get("patient").get("id"), id)));

            if (!criteria.statuses().isEmpty()) {
                predicates.add(root.get("status").in(criteria.statuses()));
            }

            if (criteria.date() != null) {
                LocalDateTime dayStart = criteria.date().atStartOfDay();
                LocalDateTime dayEndExclusive = criteria.date().plusDays(1).atStartOfDay();
                predicates.add(cb.greaterThanOrEqualTo(root.get("appointmentTime"), dayStart));
                predicates.add(cb.lessThan(root.get("appointmentTime"), dayEndExclusive));
            }

            if (criteria.from() != null) {
                LocalDateTime fromLdt = LocalDateTime.ofInstant(criteria.from(), ZONE);
                predicates.add(cb.greaterThanOrEqualTo(root.get("appointmentTime"), fromLdt));
            }

            if (criteria.to() != null) {
                LocalDateTime toLdt = LocalDateTime.ofInstant(criteria.to(), ZONE);
                predicates.add(cb.lessThanOrEqualTo(root.get("appointmentTime"), toLdt));
            }

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
