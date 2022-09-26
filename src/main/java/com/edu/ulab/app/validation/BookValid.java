package com.edu.ulab.app.validation;

import com.edu.ulab.app.dto.BookDto;

public class BookValid {
    public static boolean isValidBook(BookDto bookDto) {
        boolean validTitles = false;
        boolean validAuthor = false;
        boolean validPageCount = false;

        if (bookDto.getTitle() != null) {
            validTitles = !bookDto.getTitle().isBlank();
        }

        if (bookDto.getAuthor() != null) {
            validAuthor = !bookDto.getAuthor().isBlank();
        }

        if (bookDto.getPageCount() > 0) {
            validPageCount = true;
        }

        return validTitles && validAuthor && validPageCount;
    }
}
