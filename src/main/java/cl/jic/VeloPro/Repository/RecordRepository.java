package cl.jic.VeloPro.Repository;

import cl.jic.VeloPro.Model.Entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
}
