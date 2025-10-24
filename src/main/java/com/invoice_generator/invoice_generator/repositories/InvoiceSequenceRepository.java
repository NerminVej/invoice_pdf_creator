package com.invoice_generator.invoice_generator.repositories;

import com.invoice_generator.invoice_generator.models.InvoiceSequence;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface InvoiceSequenceRepository extends JpaRepository<InvoiceSequence, Integer> {

    // Pessimistic write lock to prevent concurrent increments
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM InvoiceSequence s WHERE s.year = :year")
    Optional<InvoiceSequence> findForUpdate(@Param("year") Integer year);
}
