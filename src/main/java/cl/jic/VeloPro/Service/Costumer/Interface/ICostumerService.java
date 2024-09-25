package cl.jic.VeloPro.Service.Costumer.Interface;

import cl.jic.VeloPro.Model.Entity.Costumer.Costumer;

import java.util.List;

public interface ICostumerService {

    void addNewCostumer(Costumer costumer);
    List<Costumer> getAll();
    void delete(Costumer costumer);
    void paymentDebt(Costumer costumer,String amount);
    void statusAssign(Costumer costumer);
    void addSaleToCostumer(Costumer costumer);
    void updateTotalDebt(Costumer costumer);
    void updateCostumer(Costumer costumer);
}
