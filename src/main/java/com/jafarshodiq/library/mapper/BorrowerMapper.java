package com.jafarshodiq.library.mapper;

import com.jafarshodiq.library.dto.response.BorrowerResponse;
import com.jafarshodiq.library.entity.Borrower;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BorrowerMapper {

    BorrowerResponse toResponse(Borrower borrower);
}
