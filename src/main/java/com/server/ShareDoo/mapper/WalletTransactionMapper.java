package com.server.ShareDoo.mapper;

import com.server.ShareDoo.entity.WalletTransaction;
import com.server.ShareDoo.dto.response.wallet.WalletTransactionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface WalletTransactionMapper {
    WalletTransactionMapper INSTANCE = Mappers.getMapper(WalletTransactionMapper.class);

    @Mapping(source = "wallet.id", target = "walletId")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "status", target = "status")
    WalletTransactionDTO toDTO(WalletTransaction transaction);
}
