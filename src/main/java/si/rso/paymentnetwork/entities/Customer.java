package si.rso.paymentnetwork.entities;

import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import si.rso.paymentnetwork.enums.CustomerStatus;

@Entity
@Table(name = "customer")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Customer {

    @Id
    @SequenceGenerator(
            name = "CUSTOMER_ID_SEQUENCE",
            sequenceName = "CUSTOMER_ID_SEQUENCE",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUSTOMER_ID_SEQUENCE")
    @Column(name = "customer_id", nullable = false, updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private long id;

    @Column(name = "customer_uuid", nullable = false, updatable = false, unique = true)
    private UUID uuid;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_status", nullable = false)
    private CustomerStatus customerStatus;

    @ManyToOne
    @JoinColumn(name = "bank_id", columnDefinition = "bank")
    private Bank bank;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE)
    private Set<Payment> payments;
}
