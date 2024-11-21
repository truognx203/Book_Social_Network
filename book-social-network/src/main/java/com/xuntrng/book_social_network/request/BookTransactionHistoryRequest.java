package com.xuntrng.book_social_network.request;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

public record BookTransactionHistoryRequest(

        @NotNull(message = "")
        Integer bookId,

        
        String note,
        Integer userId,
        String username) {
}
