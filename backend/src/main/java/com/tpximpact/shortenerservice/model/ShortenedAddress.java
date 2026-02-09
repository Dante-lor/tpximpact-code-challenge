package com.tpximpact.shortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity used to store the shortened addresses. Includes important details like uniqueness
 * and indices for fast lookup.
 */
@Entity
@Table(
    indexes = {
         @Index(columnList = "alias", unique = true),
         @Index(columnList = "original_url")
    }
)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortenedAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
    @Column(name = "alias", unique = true, nullable = false)
    private String alias;

    @Column(name = "original_url", unique = false, nullable = false)
    private String originalUrl;

}
