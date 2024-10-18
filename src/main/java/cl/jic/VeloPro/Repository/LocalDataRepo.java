package cl.jic.VeloPro.Repository;

import cl.jic.VeloPro.Model.Entity.LocalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalDataRepo extends JpaRepository<LocalData, Long> {
}
