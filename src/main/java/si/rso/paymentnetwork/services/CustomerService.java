package si.rso.paymentnetwork.services;

import io.micrometer.core.instrument.util.StringUtils;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import si.rso.paymentnetwork.dtos.CustomerDTO;
import si.rso.paymentnetwork.dtos.CustomerValidationDTO;
import si.rso.paymentnetwork.entities.Customer;
import si.rso.paymentnetwork.enums.BankStatus;
import si.rso.paymentnetwork.enums.CustomerStatus;
import si.rso.paymentnetwork.mappers.CustomerMapper;
import si.rso.paymentnetwork.repositories.CustomerRepository;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    private final BankService bankService;

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::customerToCustomerDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO findCustomerDTOByUuid(UUID uuid) {
        return customerMapper.customerToCustomerDTO(
                customerRepository
                        .findCustomerByUuid(uuid)
                        .orElseThrow(
                                () ->
                                        new ElementNotFoundException(
                                                "Customer with uuid " + uuid + " not exists.")));
    }

    public Customer findCustomerByUuid(UUID uuid) {
        return customerRepository
                .findCustomerByUuid(uuid)
                .orElseThrow(
                        () ->
                                new ElementNotFoundException(
                                        "Customer with uuid " + uuid + " not exists."));
    }

    public CustomerDTO findCustomerDTOByEmail(String email) {
        Customer customer =
                customerRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () ->
                                        new ElementNotFoundException(
                                                "Customer with email " + email + " not found."));
        return customerMapper.customerToCustomerDTO(customer);
    }

    public Customer findCustomerByEmail(String email) {
        Customer customer =
                customerRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () ->
                                        new ElementNotFoundException(
                                                "Customer with email " + email + " not found."));
        return customer;
    }

    public CustomerDTO registerNewCustomer(CustomerDTO customerDTO) {
        validate(customerDTO);
        if (customerRepository.existsCustomerByEmail(customerDTO.getEmail()))
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Customer with email " + customerDTO.getEmail() + " already exists.");

        Customer newCustomer = customerMapper.customerDTOToCustomer(customerDTO);
        newCustomer.setBank(bankService.findBankByBic(customerDTO.getBankBIC()));
        newCustomer.setCustomerStatus(CustomerStatus.ACTIVE);
        newCustomer.setUuid(UUID.randomUUID());
        customerRepository.save(newCustomer);

        return customerMapper.customerToCustomerDTO(newCustomer);
    }

    public void unregisterCustomer(UUID uuid) {
        Customer customer =
                customerRepository
                        .findCustomerByUuid(uuid)
                        .orElseThrow(
                                () ->
                                        new ElementNotFoundException(
                                                "Customer with uuid " + uuid + " not exists."));

        customer.setCustomerStatus(CustomerStatus.INACTIVE);
        customerRepository.save(customer);
    }

    @Transactional
    public CustomerDTO updateCustomer(UUID uuid, CustomerDTO newCustomerDTO) {
        validate(newCustomerDTO);
        Customer existingCustomer =
                customerRepository
                        .findCustomerByUuid(uuid)
                        .orElseThrow(
                                () ->
                                        new ElementNotFoundException(
                                                "Customer with uuid " + uuid + " not exists."));

        if (!Objects.equals(
                        existingCustomer.getEmail().toLowerCase(),
                        newCustomerDTO.getEmail().toLowerCase())
                && customerRepository.existsCustomerByEmail(newCustomerDTO.getEmail()))
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Customer with email " + newCustomerDTO.getEmail() + " already exists");

        existingCustomer.setBank(bankService.findBankByBic(newCustomerDTO.getBankBIC()));
        customerMapper.mapToCustomer(newCustomerDTO, existingCustomer);
        customerRepository.save(existingCustomer);

        return customerMapper.customerToCustomerDTO(existingCustomer);
    }

    public CustomerValidationDTO validateCustomer(String email) {
        Customer customer = findCustomerByEmail(email);

        if (customer.getCustomerStatus().equals(CustomerStatus.ACTIVE)
                && bankService
                        .findBankByBic(customer.getBank().getBic())
                        .getBankStatus()
                        .equals(BankStatus.REGISTERED))
            return new CustomerValidationDTO(email, true);
        else return new CustomerValidationDTO(email, false);
    }

    public void validate(CustomerDTO customerDTO) {
        if (StringUtils.isBlank(customerDTO.getEmail()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email");
        if (StringUtils.isBlank(customerDTO.getBankBIC()) || customerDTO.getBankBIC().length() > 11)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid bic code");
    }
}
