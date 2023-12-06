package si.rso.paymentnetwork.repositories;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import si.rso.paymentnetwork.entities.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findCustomerByUuid(UUID uuid);

    Boolean existsCustomerByEmail(String email);

    Optional<Customer> findByEmail(String email);
}
