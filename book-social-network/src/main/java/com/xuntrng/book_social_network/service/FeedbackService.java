package com.xuntrng.book_social_network.service;


import com.xuntrng.book_social_network.common.PageResponse;
import com.xuntrng.book_social_network.request.FeedbackRequest;
import com.xuntrng.book_social_network.response.FeedbackResponse;
import org.springframework.security.core.Authentication;

public interface FeedbackService {
    Integer save(FeedbackRequest request, Authentication connectedUser);

    PageResponse<FeedbackResponse> getAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser);
}
