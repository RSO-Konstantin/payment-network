package si.rso.paymentnetwork.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import si.rso.paymentnetwork.enums.PaymentStatus;

@Entity
@Table(name = "payment")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Payment {

    @Id
    @SequenceGenerator(
            name = "PAYMENT_ID_SEQUENCE",
            sequenceName = "PAYMENT_ID_SEQUENCE",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAYMENT_ID_SEQUENCE")
    @Column(name = "payment_id", nullable = false, updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private long id;

    @Column(name = "payment_uuid", nullable = false, updatable = false, unique = true)
    private UUID uuid;

    @Column(name = "receiver_email", nullable = false)
    private String receiverEmail;

    @Column(name = "source_amount", nullable = false)
    private BigDecimal sourceAmount;

    @Column(name = "source_currency", nullable = false)
    private String sourceCurrency;

    @Column(name = "target_amount", nullable = false)
    private BigDecimal targetAmount;

    @Column(name = "target_currency", nullable = false)
    private String targetCurrency;

    @Column(name = "exchange_rate", nullable = false)
    private BigDecimal exchangeRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
