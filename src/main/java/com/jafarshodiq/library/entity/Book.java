package com.jafarshodiq.library.entity;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "book", indexes = @Index(name = "idx_book_isbn", columnList = "isbn"))
public class Book  extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "isbn", nullable = false, foreignKey = @ForeignKey(name = "fk_book_isbn"))
    private IsbnCatalog isbnCatalog;

}
