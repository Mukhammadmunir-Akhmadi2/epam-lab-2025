package com.epam.infrastructure.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class TrainingMetrics {

    @PersistenceContext
    private EntityManager em;

    public TrainingMetrics(MeterRegistry registry) {
        Gauge.builder("trainings_yearly_total", this, ref ->
                em.createQuery(
                        "SELECT COUNT(t) " +
                                "FROM TrainingDao t " +
                                "WHERE YEAR(t.date) = YEAR(CURRENT_DATE)",
                        Long.class
                ).getSingleResult()
        ).register(registry);

        Gauge.builder("trainings_monthly_total", this, value ->
                em.createQuery(
                        "SELECT COUNT(t) " +
                                "FROM TrainingDao t " +
                                "WHERE MONTH(t.date) = MONTH(CURRENT_DATE)",
                        Long.class
                ).getSingleResult()
        ).register(registry);

        Gauge.builder("trainings.total", this, value ->
                em.createQuery(
                        "SELECT COUNT(t) " +
                                "FROM TrainingDao t",
                        Long.class
                ).getSingleResult()
        ).register(registry);
    }
}