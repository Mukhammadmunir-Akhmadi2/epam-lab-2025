package com.epam.infrastructure.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class TrainerMetrics {

    @PersistenceContext
    private EntityManager em;

    public TrainerMetrics(MeterRegistry registry) {

        Gauge.builder("trainers_total", this, ref ->
                em.createQuery(
                        "SELECT COUNT(t) " +
                                "FROM TrainerDao t",
                        Long.class
                        ).getSingleResult()
        ).register(registry);

        Gauge.builder("trainers_active_total", this, ref ->
                em.createQuery(
                        "SELECT COUNT(t) " +
                                "FROM TrainerDao t " +
                                "WHERE t.isActive = true",
                        Long.class
                        ).getSingleResult()
        ).register(registry);

        Gauge.builder("trainers_inactive_total", this, ref ->
                em.createQuery(
                        "SELECT COUNT(t) " +
                                "FROM TrainerDao t " +
                                "WHERE t.isActive = false",
                        Long.class
                        ).getSingleResult()
        ).register(registry);
    }
}