package cl.jic.VeloPro.Service.Sale.Interfaces;

import cl.jic.VeloPro.Model.Entity.Sale.CashRegister;

import java.util.List;

public interface ICashRegisterService {

    void addRegisterOpening(CashRegister cashRegister);
    void addRegisterClosing(CashRegister cashRegister);
    void addRegisterValidateComment(CashRegister cashRegister);
    CashRegister getRegisterByUser(Long id);
    List<CashRegister> getAll();
    void updateRegister(Long id, String status, Integer amountOpening, Integer amountClosing, Integer pos);
}
