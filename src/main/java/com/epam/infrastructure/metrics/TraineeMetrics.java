package com.epam.infrastructure.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class TraineeMetrics {

    @PersistenceContext
    private EntityManager em;

    public TraineeMetrics(MeterRegistry registry) {

        Gauge.builder("trainees_total", this, ref ->
                em.createQuery(
                        "SELECT COUNT(t) " +
                                "FROM TraineeDao t",
                        Long.class
                        ).getSingleResult()
        ).register(registry);

        Gauge.builder("trainees_active_total", this, ref ->
                em.createQuery(
                        "SELECT COUNT(t) " +
                                "FROM TraineeDao t WHERE t.isActive = true",
                        Long.class
                        ).getSingleResult()
        ).register(registry);

        Gauge.builder("trainees_inactive_total", this, ref ->
                em.createQuery(
                        "SELECT COUNT(t) " +
                                "FROM TraineeDao t " +
                                "WHERE t.isActive = false",
                        Long.class
                        ).getSingleResult()
        ).register(registry);
    }
}