package cl.jic.VeloPro.Service.Sale;

import cl.jic.VeloPro.Service.Sale.Interfaces.IPaymentService;
import org.springframework.stereotype.Service;

@Service
public class PaymentService implements IPaymentService {

    @Override
    public int mixedPayment(int amount, int total) {
        if (amount < total && amount > 0){
            return total - amount;
        }
        return -1;
    }

    @Override
    public int cashPayment(int amount, int total) {
        if (amount >= total) {
            return amount - total;
        }
        return -1;
    }
}
