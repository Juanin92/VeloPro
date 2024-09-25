package cl.jic.VeloPro.Repository.Costumer;


import cl.jic.VeloPro.Model.Entity.Costumer.Costumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CostumerRepo extends JpaRepository<Costumer,Long> {
}
