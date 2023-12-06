package si.rso.paymentnetwork.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import si.rso.paymentnetwork.enums.CustomerStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID uuid;

    private String email;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CustomerStatus customerStatus;

    private String bankBIC;
}
