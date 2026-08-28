package com.jafarshodiq.library.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

import static com.jafarshodiq.library.constant.BaseConstant.DateTimeParameter.RESPONSE_DATE_TIME_FORMAT;

public record LoanResponse(
        String id,
        String bookId,
        String bookName,
        String borrowerId,
        String borrowerEmail,

        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = RESPONSE_DATE_TIME_FORMAT
        )
        OffsetDateTime borrowedAt,

        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = RESPONSE_DATE_TIME_FORMAT
        )
        OffsetDateTime returnedAt
) {
}