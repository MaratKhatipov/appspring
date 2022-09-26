package com.edu.ulab.app.validation;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;

public class PersonValid {
    public static boolean isValidPerson(UserDto userDto) {
        boolean validation = false;
        if (userDto.getFullName() != null) {
            if (!userDto.getFullName().isBlank() && userDto.getAge() > 0) {
                validation = true;
            }
        }
        return validation;
    }
}
