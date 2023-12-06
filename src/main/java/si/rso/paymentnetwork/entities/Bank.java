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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import si.rso.paymentnetwork.enums.BankStatus;

@Entity
@Table(name = "bank")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Bank {
    @Id
    @SequenceGenerator(
            name = "BANK_ID_SEQUENCE",
            sequenceName = "BANK_ID_SEQUENCE",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BANK_ID_SEQUENCE")
    @Column(name = "bank_id", nullable = false, updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private long id;

    @Column(name = "bank_uuid", nullable = false, updatable = false, unique = true)
    private UUID uuid;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "bank_bic", nullable = false, unique = true)
    private String bic;

    @Column(name = "bank_currency", nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "bank_status", nullable = false)
    private BankStatus bankStatus;

    @OneToMany(mappedBy = "bank", cascade = CascadeType.REMOVE)
    private Set<Customer> customers;
}
