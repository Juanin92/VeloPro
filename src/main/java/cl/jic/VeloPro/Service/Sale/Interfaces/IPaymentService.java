package cl.jic.VeloPro.Service.Sale.Interfaces;

public interface IPaymentService {
    int mixedPayment(int amount, int total);
    int cashPayment(int amount, int total);
}
