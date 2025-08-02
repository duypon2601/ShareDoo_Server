package com.server.ShareDoo.mapper;

import com.server.ShareDoo.entity.Wallet;
import com.server.ShareDoo.dto.response.wallet.WalletDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    WalletMapper INSTANCE = Mappers.getMapper(WalletMapper.class);

    @Mapping(source = "user.userId", target = "userId")
    WalletDTO toDTO(Wallet wallet);
}
