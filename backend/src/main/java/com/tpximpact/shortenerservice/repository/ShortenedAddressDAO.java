package com.tpximpact.shortenerservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpximpact.shortenerservice.model.ShortenedAddress;

@Repository
public interface ShortenedAddressDAO extends JpaRepository<ShortenedAddress, Long> {

    Optional<ShortenedAddress> findByAlias(String alias);
    
}
