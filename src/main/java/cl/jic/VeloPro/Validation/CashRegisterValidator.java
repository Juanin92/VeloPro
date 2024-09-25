package cl.jic.VeloPro.Validation;

import cl.jic.VeloPro.Model.Entity.Sale.CashRegister;
import org.springframework.stereotype.Component;

@Component
public class CashRegisterValidator {
    public void validateClosing(CashRegister cashRegister){
        validateAmountClosing(cashRegister.getAmountClosingCash());
        validateAmountPos(cashRegister.getAmountClosingPos());
    }
    public void validateOpening(CashRegister cashRegister){
        if (cashRegister.getAmountOpening() <= 0){
            throw new IllegalArgumentException("El valor debe ser mayor a 0");
        }
    }

    private void validateAmountClosing(int amount){
        if (amount <= 0){
            throw new IllegalArgumentException("El valor debe ser mayor a 0");
        }
    }
    private void validateAmountPos(int amount){
        if (amount <= 0){
            throw new IllegalArgumentException("El valor debe ser mayor a 0");
        }
    }
}
