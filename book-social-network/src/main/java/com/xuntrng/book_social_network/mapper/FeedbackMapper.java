package com.xuntrng.book_social_network.mapper;


import com.xuntrng.book_social_network.entity.Book;
import com.xuntrng.book_social_network.entity.Feedback;
import com.xuntrng.book_social_network.request.FeedbackRequest;
import com.xuntrng.book_social_network.response.FeedbackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {
    public Feedback toFeedback(FeedbackRequest request) {
        return Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .book(Book.builder().id(request.bookId()).build())
                .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer id) {
        return FeedbackResponse.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), String.valueOf(id)))
                .build();
    }
}
