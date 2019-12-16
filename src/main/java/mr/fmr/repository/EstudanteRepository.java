package mr.fmr.repository;

import mr.fmr.model.Estudante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstudanteRepository extends JpaRepository<Estudante, Long> {

    @Query(value = "SELECT e.* FROM user e WHERE e.id NOT IN (SELECT DISTINCT morador_id FROM morador_republica) AND dtype='Estudante'", nativeQuery = true)
    List<Estudante> findByMoradorRepublicaIsNull();
    List<Estudante> findAllByMoradorRepublica_Aprovado(boolean aprovado);

}
