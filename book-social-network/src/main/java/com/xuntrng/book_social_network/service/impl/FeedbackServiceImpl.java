package com.xuntrng.book_social_network.service.impl;

import com.xuntrng.book_social_network.common.PageResponse;
import com.xuntrng.book_social_network.entity.Book;
import com.xuntrng.book_social_network.entity.Feedback;
import com.xuntrng.book_social_network.entity.User;
import com.xuntrng.book_social_network.exception.OperationNotPermittedException;
import com.xuntrng.book_social_network.mapper.FeedbackMapper;
import com.xuntrng.book_social_network.repository.BookRepository;
import com.xuntrng.book_social_network.repository.FeedbackRepository;
import com.xuntrng.book_social_network.request.FeedbackRequest;
import com.xuntrng.book_social_network.response.FeedbackResponse;
import com.xuntrng.book_social_network.service.FeedbackService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;

    @Override
    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with id = " + request.bookId()));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You cannot feedback to an archived or not shareable book ");
        }
        if (Objects.equals(book.getCreatedBy(), connectedUser.getName())) {
            throw new OperationNotPermittedException("You cannot give a feedback to your own book");
        }
        Feedback feedback = feedbackMapper.toFeedback(request);
        return feedbackRepository.save(feedback).getId();
    }

    @Override
    public PageResponse<FeedbackResponse> getAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);
        User user = (User) connectedUser.getPrincipal();
        Page<Feedback> feedbacks = feedbackRepository.getAllFeedbacksByBook(bookId, pageable);
        List<FeedbackResponse> responseList = feedbacks.stream()
                .map(feedback -> feedbackMapper.toFeedbackResponse(feedback, user.getId())).toList();
        return new PageResponse<>(
                responseList,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
