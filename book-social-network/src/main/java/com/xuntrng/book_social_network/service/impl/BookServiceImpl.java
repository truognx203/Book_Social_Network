package com.xuntrng.book_social_network.service.impl;

import com.xuntrng.book_social_network.common.PageResponse;
import com.xuntrng.book_social_network.entity.Book;
import com.xuntrng.book_social_network.entity.BookTransactionHistory;
import com.xuntrng.book_social_network.entity.User;
import com.xuntrng.book_social_network.exception.OperationNotPermittedException;
import com.xuntrng.book_social_network.mapper.BookMapper;
import com.xuntrng.book_social_network.repository.BookRepository;
import com.xuntrng.book_social_network.repository.BookTransactionHistoryRepository;
import com.xuntrng.book_social_network.request.BookRequest;
import com.xuntrng.book_social_network.response.BookResponse;
import com.xuntrng.book_social_network.response.BorrowedBookResponse;
import com.xuntrng.book_social_network.service.BookService;
import com.xuntrng.book_social_network.utils.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
    private final FileStorageService fileStorageService;

    @Override
    public Integer save(BookRequest request, Authentication connectedUser) {
        Book book = bookMapper.toBook(request);
        book.setOwner((User) connectedUser.getPrincipal());
        return bookRepository.save(book).getId();
    }

    @Override
    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
    }

    @Override
    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.fullName());
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse).toList();
        return new PageResponse<>(
                bookResponses
                , books.getNumber()
                , books.getSize()
                , books.getTotalElements()
                , books.getTotalPages()
                , books.isFirst()
                , books.isLast());
    }

    @Override
    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllBooksByOwner(pageable, user.getName());
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse).toList();
        return new PageResponse<>(
                bookResponses
                , books.getNumber()
                , books.getSize()
                , books.getTotalElements()
                , books.getTotalPages()
                , books.isFirst()
                , books.isLast());

    }

    @Override
    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable, connectedUser.getName());
        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream()
                .map(bookMapper::toBookBorrowedResponse).toList();
        return new PageResponse<>(
                bookResponses
                , allBorrowedBooks.getNumber()
                , allBorrowedBooks.getSize()
                , allBorrowedBooks.getTotalElements()
                , allBorrowedBooks.getTotalPages()
                , allBorrowedBooks.isFirst()
                , allBorrowedBooks.isLast());
    }

    @Override
    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allReturnedBooks = bookTransactionHistoryRepository.findAllReturnedBooks(pageable, connectedUser.getName());
        List<BorrowedBookResponse> bookResponses = allReturnedBooks.stream()
                .map(bookMapper::toBookBorrowedResponse).toList();
        return new PageResponse<>(
                bookResponses
                , allReturnedBooks.getNumber()
                , allReturnedBooks.getSize()
                , allReturnedBooks.getTotalElements()
                , allReturnedBooks.getTotalPages()
                , allReturnedBooks.isFirst()
                , allReturnedBooks.isLast());
    }

    @Override
    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id = " + bookId));
        if (!Objects.equals(book.getCreatedBy(), connectedUser.getName())) {
            throw new OperationNotPermittedException("You can't update others books shareable status");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    @Override
    public Integer updateArchiveStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id = " + bookId));
        if (!Objects.equals(book.getCreatedBy(), connectedUser.getName())) {
            throw new OperationNotPermittedException("You can't update others books shareable status");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    @Override
    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id = " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The request book is archived or not shareable");
        }
        if (!Objects.equals(book.getCreatedBy(), connectedUser.getName())) {
            throw new OperationNotPermittedException("You can't update others books shareable status");
        }
        final boolean isBorrowed = bookTransactionHistoryRepository.isAlreadyBorrowedByUser(bookId, connectedUser.getName());
        if (isBorrowed) {
            throw new OperationNotPermittedException("The request book cannot be borrowed since it is already borrowed by another user");
        }
        BookTransactionHistory history = BookTransactionHistory.builder()
                .book(book)
                .borrowedDate(LocalDateTime.now())
                .userId(connectedUser.getName())
                .returned(false)
                .returnApproved(false)
                .build();
        return bookTransactionHistoryRepository.save(history).getId();
    }

    @Override
    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id = " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The request book is archived or not shareable");
        }
        if (!Objects.equals(book.getCreatedBy(), connectedUser.getName())) {
            throw new OperationNotPermittedException("You can't update others books shareable status");
        }
        BookTransactionHistory historyBook = bookTransactionHistoryRepository.findByBookIdAndUserId(bookId, connectedUser.getName())
                .orElseThrow(() -> new OperationNotPermittedException("You didn't borrow this book"));
        historyBook.setReturned(true);
        historyBook.setBorrowedDate(LocalDateTime.now());
        return bookTransactionHistoryRepository.save(historyBook).getId();
    }

    @Override
    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id = " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The request book is archived or not shareable");
        }
        if (!Objects.equals(book.getCreatedBy(), connectedUser.getName())) {
            throw new OperationNotPermittedException("You cannot approve the return of a book you do not own");
        }
        BookTransactionHistory history = bookTransactionHistoryRepository.findByBookIdAndOwnerId(bookId, connectedUser.getName())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet. You cannot approve its return"));

        history.setReturnApproved(true);
        return bookTransactionHistoryRepository.save(history).getId();
    }

    @Override
    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id = " + bookId));
        var picCoverPicture = fileStorageService.saveFile(file, connectedUser.getName());
        book.setBookCover(picCoverPicture);
        bookRepository.save(book);
    }
}
