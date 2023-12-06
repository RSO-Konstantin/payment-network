package si.rso.paymentnetwork.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import si.rso.paymentnetwork.dtos.PaymentDTO;
import si.rso.paymentnetwork.entities.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "customer.bankBIC", source = "customer.bank.bic")
    PaymentDTO paymentToPaymentDTO(Payment payment);

    Payment paymentDTOToPayment(PaymentDTO paymentDTO);

    void mapToPayment(PaymentDTO paymentDTO, @MappingTarget Payment payment);

    void mapToPaymentDTO(Payment payment, @MappingTarget PaymentDTO paymentDTO);
}
