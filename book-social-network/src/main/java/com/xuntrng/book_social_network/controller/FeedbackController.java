package com.xuntrng.book_social_network.controller;


import com.xuntrng.book_social_network.common.PageResponse;
import com.xuntrng.book_social_network.request.FeedbackRequest;
import com.xuntrng.book_social_network.response.FeedbackResponse;
import com.xuntrng.book_social_network.service.FeedbackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedbacks")
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Integer> saveFeedback(@Valid @RequestBody FeedbackRequest request, Authentication connectedUser) {
        return ResponseEntity.ok(feedbackService.save(request, connectedUser));
    }

    @GetMapping("/book/{book-id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> getAllFeedbackByBook(@PathVariable("book-id") Integer bookId,
                                                                               @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                                               @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                                               Authentication connectedUser) {
        return ResponseEntity.ok(feedbackService.getAllFeedbacksByBook(bookId, page, size, connectedUser));
    }
}
