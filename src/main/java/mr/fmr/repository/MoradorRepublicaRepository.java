package mr.fmr.repository;

import mr.fmr.model.MoradorRepublica;
import mr.fmr.model.MoradorRepublicaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MoradorRepublicaRepository extends JpaRepository<MoradorRepublica, MoradorRepublicaId> {

    @Modifying
    @Query("DELETE FROM MoradorRepublica MR  WHERE MR.id = :id")
    void deleteById(MoradorRepublicaId id);
}
