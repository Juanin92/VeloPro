package cl.jic.VeloPro.Repository;

import cl.jic.VeloPro.Model.Entity.Kardex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KardexRepo extends JpaRepository<Kardex, Long> {
}
