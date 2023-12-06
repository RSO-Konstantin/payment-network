package si.rso.paymentnetwork.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import si.rso.paymentnetwork.dtos.CustomerDTO;
import si.rso.paymentnetwork.entities.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "bankBIC", source = "bank.bic")
    CustomerDTO customerToCustomerDTO(Customer customer);

    Customer customerDTOToCustomer(CustomerDTO customerDTO);

    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "customerStatus", ignore = true)
    void mapToCustomer(CustomerDTO customerDTO, @MappingTarget Customer customer);

    void mapToCustomerDTO(Customer customer, @MappingTarget CustomerDTO customerDTO);
}
