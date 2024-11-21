package com.xuntrng.book_social_network.repository;

import com.xuntrng.book_social_network.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    @Query("""
            SELECT feedback from Feedback feedback
            WHERE feedback.book.id =: bookId
            """)
    Page<Feedback> getAllFeedbacksByBook(Integer bookId, Pageable pageable);
}
