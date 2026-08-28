package com.jafarshodiq.library.mapper;

import com.jafarshodiq.library.dto.response.BookResponse;
import com.jafarshodiq.library.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "isbn", source = "book.isbnCatalog.isbn")
    @Mapping(target = "title", source = "book.isbnCatalog.title")
    @Mapping(target = "author", source = "book.isbnCatalog.author")
    @Mapping(target = "available", source = "available")
    BookResponse toResponse(
            Book book,
            boolean available
    );
}