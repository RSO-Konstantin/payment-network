package si.rso.paymentnetwork.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import si.rso.paymentnetwork.dtos.BankDTO;
import si.rso.paymentnetwork.services.BankService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/banks")
public class BankController {

    private final BankService bankService;

    @Operation(summary = "Get all banks or query by name")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Bank entities found",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array =
                                            @ArraySchema(
                                                    schema =
                                                            @Schema(
                                                                    implementation =
                                                                            BankDTO.class)))
                        }),
                @ApiResponse(
                        responseCode = "404",
                        description = "Bank entity not found",
                        content = @Content)
            })
    @GetMapping
    public List<BankDTO> getAllBanks(
            @RequestParam(name = "bankName", required = false) String name) {
        return name == null ? bankService.getAllBanks() : bankService.getBankByNameContaining(name);
    }

    @Operation(summary = "Get a bank by UUID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Bank entity found",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array =
                                            @ArraySchema(
                                                    schema =
                                                            @Schema(
                                                                    implementation =
                                                                            BankDTO.class)))
                        }),
                @ApiResponse(
                        responseCode = "404",
                        description = "Bank entity not found",
                        content = @Content)
            })
    @GetMapping("/{bankUuid}")
    public BankDTO getByUuid(@PathVariable("bankUuid") UUID uuid) {
        return bankService.findBankByUuid(uuid);
    }

    @Operation(summary = "Register new bank to the network")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Bank is registered",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array =
                                            @ArraySchema(
                                                    schema =
                                                            @Schema(
                                                                    implementation =
                                                                            BankDTO.class)))
                        }),
                @ApiResponse(
                        responseCode = "409",
                        description = "Bank with BIC already exists",
                        content = @Content)
            })
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BankDTO> registerNewBank(@RequestBody BankDTO bank) {
        BankDTO savedBank = bankService.registerNewBank(bank);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{uuid}")
                        .buildAndExpand(savedBank.getUuid())
                        .toUri();
        return ResponseEntity.created(location).body(savedBank);
    }

    @Operation(summary = "Unregister a bank from the network")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Bank is unregistered"),
                @ApiResponse(
                        responseCode = "404",
                        description = "Bank entity not found",
                        content = @Content)
            })
    @DeleteMapping(path = "/{bankUuid}")
    public ResponseEntity<Void> unregisterBank(@PathVariable("bankUuid") UUID uuid) {
        bankService.unregisterBank(uuid);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a bank by its UUID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Bank was updated",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array =
                                            @ArraySchema(
                                                    schema =
                                                            @Schema(
                                                                    implementation =
                                                                            BankDTO.class)))
                        }),
                @ApiResponse(
                        responseCode = "404",
                        description = "Bank not found",
                        content = @Content),
                @ApiResponse(
                        responseCode = "409",
                        description = "Bank with BIC already exists",
                        content = @Content)
            })
    @PutMapping(path = "/{bankUuid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public BankDTO updateBank(
            @PathVariable("bankUuid") UUID uuid, @RequestBody BankDTO newBankEntity) {
        return bankService.updateBank(uuid, newBankEntity);
    }
}
