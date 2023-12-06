package si.rso.paymentnetwork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class PaymentNetworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentNetworkApplication.class, args);
    }
}
