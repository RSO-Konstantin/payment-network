package si.rso.paymentnetwork.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import si.rso.paymentnetwork.entities.Payment;
import si.rso.paymentnetwork.enums.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findPaymentByUuid(UUID uuid);

    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);
}
