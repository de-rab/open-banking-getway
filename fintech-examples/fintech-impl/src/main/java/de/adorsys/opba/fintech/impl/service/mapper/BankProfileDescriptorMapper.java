package de.adorsys.opba.fintech.impl.service.mapper;

import de.adorsys.opba.fintech.api.model.generated.BankProfile;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankProfileDescriptor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BankProfileDescriptorMapper {
    @Mapping(target = "services", source = "serviceList")
    BankProfile map(BankProfileDescriptor bankProfileDescriptor);
}
