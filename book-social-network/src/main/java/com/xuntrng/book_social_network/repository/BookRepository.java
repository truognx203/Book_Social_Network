package com.xuntrng.book_social_network.repository;

import com.xuntrng.book_social_network.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    @Query("""
            SELECT book
            FROM Book book
            WHERE book.archived = false
            AND book.shareable = true
            AND book.createdBy != :fullName
            """)
    Page<Book> findAllDisplayableBooks(Pageable pageable, String fullName);

    @Query("""
            SELECT book
            FROM Book book
            WHERE book.createdBy =: fullName
            """)
    Page<Book> findAllBooksByOwner(Pageable pageable, String name);
}
