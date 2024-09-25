package cl.jic.VeloPro.Service.Sale;

import cl.jic.VeloPro.Model.Entity.Sale.CashRegister;
import cl.jic.VeloPro.Repository.Sale.CashRegisterRepo;
import cl.jic.VeloPro.Service.Sale.Interfaces.ICashRegisterService;
import cl.jic.VeloPro.Validation.CashRegisterValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CashRegisterService implements ICashRegisterService {
    @Autowired private CashRegisterRepo cashRegisterRepo;
    @Autowired private CashRegisterValidator validator;

    @Override
    public void addRegisterOpening(CashRegister cashRegister) {
        validator.validateOpening(cashRegister);
        cashRegister.setDateClosing(null);
        cashRegister.setAmountClosingCash(0);
        cashRegister.setAmountClosingPos(0);
        cashRegister.setDateOpening(LocalDateTime.now());
        cashRegister.setStatus("OPEN");
        cashRegister.setUser(cashRegister.getUser());
        cashRegisterRepo.save(cashRegister);
    }

    @Override
    public void addRegisterClosing(CashRegister cashRegister) {
        validator.validateClosing(cashRegister);
        if (cashRegister.getAmountClosingCash() < cashRegister.getAmountOpening()) {
            throw new IllegalArgumentException("El monto de cierre en efectivo es menor a la apertura.");
        }
        cashRegister.setAmountClosingCash(cashRegister.getAmountClosingCash());
        cashRegister.setAmountClosingPos(cashRegister.getAmountClosingPos());
        cashRegister.setDateClosing(LocalDateTime.now());
        cashRegister.setStatus("CLOSED");
        cashRegister.setComment(null);
        cashRegisterRepo.save(cashRegister);
    }

    @Override
    public void addRegisterValidateComment(CashRegister cashRegister) {
        if (cashRegister.getComment() != null){
            cashRegister.setAmountClosingCash(cashRegister.getAmountClosingCash());
            cashRegister.setAmountClosingPos(cashRegister.getAmountClosingPos());
            cashRegister.setDateClosing(LocalDateTime.now());
            cashRegister.setStatus("CLOSED");
            cashRegister.setComment(cashRegister.getComment());
            cashRegisterRepo.save(cashRegister);
        }else {
            throw new IllegalArgumentException("Debes agregar un comentario para registrar.");
        }
    }

    @Override
    public CashRegister getRegisterByUser(Long id) {
        CashRegister register = cashRegisterRepo.findLatestOpenRegisterByUser(id);
        if (register == null) {
            throw new IllegalArgumentException("No hay registro de apertura válido.");
        }
        if (register.getDateOpening().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha no coincide con la apertura.");
        }
        return register;
    }

    @Override
    public List<CashRegister> getAll() {
        return cashRegisterRepo.findAll();
    }

    @Override
    public void updateRegister(Long id, String status, Integer amountOpening, Integer amountClosing, Integer pos) {
        Optional<CashRegister> cashRegister = cashRegisterRepo.findById(id);
        if (cashRegister.isPresent()){
            if (amountOpening != null){
                cashRegister.get().setAmountOpening(amountOpening);
                if (cashRegister.get().getDateOpening() == null) {
                    cashRegister.get().setDateOpening(LocalDateTime.now());
                }
            } else if (pos != null) {
                cashRegister.get().setAmountClosingPos(pos);
            } else {
                cashRegister.get().setStatus(status);
                cashRegister.get().setAmountClosingCash(amountClosing);
                if (cashRegister.get().getDateClosing() == null) {
                    cashRegister.get().setDateClosing(LocalDateTime.now());
                }
            }
            cashRegisterRepo.save(cashRegister.get());
        }
    }
}
