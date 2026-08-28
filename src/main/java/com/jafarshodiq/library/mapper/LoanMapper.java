package com.jafarshodiq.library.mapper;

import com.jafarshodiq.library.dto.response.LoanResponse;
import com.jafarshodiq.library.entity.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookName", source = "book.isbnCatalog.title")
    @Mapping(target = "borrowerId", source = "borrower.id")
    @Mapping(target = "borrowerEmail", source = "borrower.email")
    LoanResponse toResponse(Loan loan);
}