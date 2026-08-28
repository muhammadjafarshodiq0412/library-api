package com.jafarshodiq.library.entity;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "borrower", uniqueConstraints = @UniqueConstraint(name = "uk_borrower_email", columnNames = "email"))
public class Borrower extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 320)
    private String email;
}
