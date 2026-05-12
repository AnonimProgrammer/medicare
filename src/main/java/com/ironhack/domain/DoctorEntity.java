package com.ironhack.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name="doctors")
@Getter
@Setter
public class DoctorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="full_name", nullable=false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Specialty specialty;
}
