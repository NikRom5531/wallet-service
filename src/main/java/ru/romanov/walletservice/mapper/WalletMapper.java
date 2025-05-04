package ru.romanov.walletservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.romanov.walletservice.model.Wallet;
import ru.romanov.walletservice.model.dto.WalletResponse;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    @Mapping(source = "id", target = "walletId")
    WalletResponse map(Wallet wallet);
}
