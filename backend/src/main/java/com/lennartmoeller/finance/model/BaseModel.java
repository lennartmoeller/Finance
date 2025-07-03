package com.lennartmoeller.finance.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.sql.Timestamp;
import java.time.Instant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@MappedSuperclass
public abstract class BaseModel {
    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp modifiedAt;

    @PrePersist
    protected void onCreate() {
        Timestamp now = Timestamp.from(Instant.now());
        createdAt = now;
        modifiedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedAt = Timestamp.from(Instant.now());
    }
}
