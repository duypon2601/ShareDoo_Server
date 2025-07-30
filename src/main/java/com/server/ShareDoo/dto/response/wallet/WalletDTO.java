package com.server.ShareDoo.dto.response.wallet;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletDTO {
    private Long id;
    private Integer userId;
    private BigDecimal balance;
}
