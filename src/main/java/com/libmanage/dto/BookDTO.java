package com.libmanage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.libmanage.model.Book;
import lombok.Data;
import lombok.NoArgsConstructor;

public class BookDTO {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("authorName")
    private String authorName;

    @JsonProperty("genreName")
    private String genreName;

    @JsonProperty("isbn")
    private String isbn;

    @JsonProperty("availableCopies")
    private int availableCopies;

    public BookDTO() {
    }

    public static BookDTO fromEntity(Book book) {
        return new BookDTO(
                book.id(),
                book.getTitle(),
                book.getAuthor().getName(),
                book.getGenre().getName(),
                book.getIsbn(),
                book.getAvailableCopies()
        );
    }

    public BookDTO(Integer id, String title, String authorName, String genreName, String isbn, int availableCopies) {
        this.id = id;
        this.title = title;
        this.authorName = authorName;
        this.genreName = genreName;
        this.isbn = isbn;
        this.availableCopies = availableCopies;
    }

    @Override
    public String toString() {
        return "BookDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", authorName='" + authorName + '\'' +
                ", genreName='" + genreName + '\'' +
                ", isbn='" + isbn + '\'' +
                ", availableCopies=" + availableCopies +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }
}