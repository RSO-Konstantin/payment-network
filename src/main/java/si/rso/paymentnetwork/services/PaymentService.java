package si.rso.paymentnetwork.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import si.rso.paymentnetwork.dtos.PaymentDTO;
import si.rso.paymentnetwork.mappers.PaymentMapper;
import si.rso.paymentnetwork.repositories.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final CustomerService customerService;

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::paymentToPaymentDTO)
                .collect(Collectors.toList());
    }

    public PaymentDTO findPaymentByUuid(UUID uuid) {
        return paymentMapper.paymentToPaymentDTO(
                paymentRepository
                        .findPaymentByUuid(uuid)
                        .orElseThrow(
                                () ->
                                        new ElementNotFoundException(
                                                "Payment with uuid " + uuid + " not exists.")));
    }
}
