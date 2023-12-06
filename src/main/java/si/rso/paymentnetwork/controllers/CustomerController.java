package si.rso.paymentnetwork.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.net.URI;
import java.util.Arrays;
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
import si.rso.paymentnetwork.dtos.CustomerDTO;
import si.rso.paymentnetwork.dtos.CustomerValidationDTO;
import si.rso.paymentnetwork.services.CustomerService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Get all customers or query by email")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Customer entities found",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array =
                                            @ArraySchema(
                                                    schema =
                                                            @Schema(
                                                                    implementation =
                                                                            CustomerDTO.class)))
                        }),
                @ApiResponse(
                        responseCode = "404",
                        description = "Customer entity not found",
                        content = @Content)
            })
    @GetMapping
    public List<CustomerDTO> getAllCustomers(
            @RequestParam(name = "customerEmail", required = false) String email) {
        return email == null
                ? customerService.getAllCustomers()
                : Arrays.asList(customerService.findCustomerDTOByEmail(email));
    }

    @Operation(summary = "Get a customer by UUID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Customer entity found",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array =
                                            @ArraySchema(
                                                    schema =
                                                            @Schema(
                                                                    implementation =
                                                                            CustomerDTO.class)))
                        }),
                @ApiResponse(
                        responseCode = "404",
                        description = "Customer entity not found",
                        content = @Content)
            })
    @GetMapping("/{customerUuid}")
    public CustomerDTO getByUuid(@PathVariable("customerUuid") UUID uuid) {
        return customerService.findCustomerDTOByUuid(uuid);
    }

    @Operation(summary = "Register a new customer to the network")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Customer is registered",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array =
                                            @ArraySchema(
                                                    schema =
                                                            @Schema(
                                                                    implementation =
                                                                            CustomerDTO.class)))
                        }),
                @ApiResponse(
                        responseCode = "409",
                        description = "Customer with email already exists",
                        content = @Content)
            })
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerDTO> registerNewCustomer(@RequestBody CustomerDTO customer) {
        CustomerDTO savedCustomer = customerService.registerNewCustomer(customer);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{uuid}")
                        .buildAndExpand(savedCustomer.getUuid())
                        .toUri();
        return ResponseEntity.created(location).body(savedCustomer);
    }

    @Operation(summary = "Unregister a customer from the network")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Customer is unregistered"),
                @ApiResponse(
                        responseCode = "404",
                        description = "Customer entity not found",
                        content = @Content)
            })
    @DeleteMapping(path = "/{customerUuid}")
    public ResponseEntity<Void> unregisterCustomer(@PathVariable("customerUuid") UUID uuid) {
        customerService.unregisterCustomer(uuid);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a customer by its UUID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Customer was updated",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array =
                                            @ArraySchema(
                                                    schema =
                                                            @Schema(
                                                                    implementation =
                                                                            CustomerDTO.class)))
                        }),
                @ApiResponse(
                        responseCode = "404",
                        description = "Customer not found",
                        content = @Content),
                @ApiResponse(
                        responseCode = "409",
                        description = "Customer with email already exists",
                        content = @Content)
            })
    @PutMapping(path = "/{customerUuid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CustomerDTO updateCustomer(
            @PathVariable("customerUuid") UUID uuid, @RequestBody CustomerDTO newCustomerEntity) {
        return customerService.updateCustomer(uuid, newCustomerEntity);
    }

    @Operation(summary = "Validates a customer")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = {
                @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        array =
                                @ArraySchema(
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                CustomerValidationDTO.class)))
            })
    @GetMapping(path = "/{customerEmail}/validate")
    public CustomerValidationDTO validateCustomer(@PathVariable("customerEmail") String email) {
        return customerService.validateCustomer(email);
    }
}
