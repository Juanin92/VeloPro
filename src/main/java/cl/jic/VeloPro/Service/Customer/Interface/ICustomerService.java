package cl.jic.VeloPro.Service.Customer.Interface;

import cl.jic.VeloPro.Model.Entity.Customer.Customer;

import java.util.List;

public interface ICustomerService {

    void addNewCustomer(Customer customer);
    List<Customer> getAll();
    void delete(Customer customer);
    void paymentDebt(Customer customer,String amount);
    void statusAssign(Customer customer);
    void addSaleToCustomer(Customer customer);
    void updateTotalDebt(Customer customer);
    void updateCustomer(Customer customer);
}
