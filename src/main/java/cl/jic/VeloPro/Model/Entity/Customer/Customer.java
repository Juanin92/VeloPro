package cl.jic.VeloPro.Model.Entity.Customer;

import cl.jic.VeloPro.Model.Enum.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String phone;
    private String email;
    private int debt;
    private int totalDebt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private boolean account;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<PaymentCustomer> paymentCustomerList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<TicketHistory> ticketHistoryList = new ArrayList<>();

    @Override
    public String toString() {
        return name + " " + surname;
    }
}
