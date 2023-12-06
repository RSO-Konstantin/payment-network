package si.rso.paymentnetwork.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import si.rso.paymentnetwork.dtos.PaymentDTO;
import si.rso.paymentnetwork.enums.PaymentStatus;
import si.rso.paymentnetwork.services.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Returns a list of all payments or query by status")
    @ApiResponse(
            responseCode = "200",
            description = "Payment entities returned",
            content = {
                @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        array = @ArraySchema(schema = @Schema(implementation = PaymentDTO.class)))
            })
    @GetMapping
    public List<PaymentDTO> getAllPayments(
            @RequestParam(name = "paymentStatus", required = false) PaymentStatus status) {
        return status == null ? paymentService.getAllPayments() : null;
    }

    @Operation(summary = "Returns an entity of payment")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Payment entity found",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array =
                                            @ArraySchema(
                                                    schema =
                                                            @Schema(
                                                                    implementation =
                                                                            PaymentDTO.class)))
                        }),
                @ApiResponse(
                        responseCode = "404",
                        description = "Payment entity not found",
                        content = @Content)
            })
    @GetMapping("/{paymentUuid}")
    public PaymentDTO getByUuid(@PathVariable("paymentUuid") UUID uuid) {
        return paymentService.findPaymentByUuid(uuid);
    }
}
