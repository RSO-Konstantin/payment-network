package si.rso.paymentnetwork.services;

import io.micrometer.core.instrument.util.StringUtils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import si.rso.paymentnetwork.dtos.BankDTO;
import si.rso.paymentnetwork.entities.Bank;
import si.rso.paymentnetwork.enums.BankStatus;
import si.rso.paymentnetwork.mappers.BankMapper;
import si.rso.paymentnetwork.repositories.BankRepository;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final BankMapper bankMapper;

    public List<BankDTO> getAllBanks() {
        return bankRepository.findAll().stream()
                .map(bankMapper::bankToBankDTO)
                .collect(Collectors.toList());
    }

    public BankDTO findBankByUuid(UUID uuid) {
        return bankMapper.bankToBankDTO(
                bankRepository
                        .findBankByUuid(uuid)
                        .orElseThrow(
                                () ->
                                        new ElementNotFoundException(
                                                "Bank with uuid " + uuid + " not exists.")));
    }

    public Bank findBankByBic(String bic) {
        return bankRepository
                .findBankByBic(bic)
                .orElseThrow(
                        () ->
                                new ElementNotFoundException(
                                        "Bank with bic " + bic + " not exists."));
    }

    public List<BankDTO> getBankByNameContaining(String name) {
        return Optional.of(
                        bankRepository.findByBankNameContaining(name).stream()
                                .map(bankMapper::bankToBankDTO)
                                .collect(Collectors.toList()))
                .filter(list -> !list.isEmpty())
                .orElseThrow(
                        () ->
                                new ElementNotFoundException(
                                        "Bank with name " + name + " not found."));
    }

    public BankDTO registerNewBank(BankDTO bankDTO) {
        validate(bankDTO);
        if (bankRepository.existsBankByBic(bankDTO.getBic()))
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bank with bic " + bankDTO.getBic() + " already exists in the network.");

        Bank newBank = bankMapper.bankDTOToBank(bankDTO);
        newBank.setBankStatus(BankStatus.REGISTERED);
        newBank.setUuid(UUID.randomUUID());
        bankRepository.save(newBank);

        return bankMapper.bankToBankDTO(newBank);
    }

    public void unregisterBank(UUID uuid) {
        Bank bank =
                bankRepository
                        .findBankByUuid(uuid)
                        .orElseThrow(
                                () ->
                                        new ElementNotFoundException(
                                                String.format(
                                                        "Bank with uuid %s not exists.", uuid)));

        bank.setBankStatus(BankStatus.UNREGISTERED);
        bankRepository.save(bank);
    }

    @Transactional
    public BankDTO updateBank(UUID uuid, BankDTO newBankDTO) {
        validate(newBankDTO);
        Bank existingBank =
                bankRepository
                        .findBankByUuid(uuid)
                        .orElseThrow(
                                () ->
                                        new ElementNotFoundException(
                                                "Bank with uuid " + uuid + " not exists."));

        if (!Objects.equals(existingBank.getBic(), newBankDTO.getBic())
                && bankRepository.existsBankByBic(newBankDTO.getBic()))
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bank with bic " + newBankDTO.getBic() + " already exists");

        bankMapper.mapToBank(newBankDTO, existingBank);

        bankRepository.save(existingBank);

        return bankMapper.bankToBankDTO(existingBank);
    }

    public void validate(BankDTO bankDTO) {
        if (StringUtils.isBlank(bankDTO.getBankName()) || bankDTO.getBankName().length() > 255)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid name");
        if (StringUtils.isBlank(bankDTO.getBic()) || bankDTO.getBic().length() > 11)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid bic code");
        if (StringUtils.isBlank(bankDTO.getCurrency()) || bankDTO.getCurrency().length() > 3)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid currency code");
    }
}
