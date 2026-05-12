package com.ironhack.infra.adapter.mapper.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.WARN,
        typeConversionPolicy = ReportingPolicy.WARN
)
public interface MapStructMapperConfig {
}

