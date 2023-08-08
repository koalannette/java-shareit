package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя пользователя не может быть пустым.")
    private String name;
    @Email(message = "Некорректный E-mail.")
    @NotBlank(message = "Почта не может быть пустой")
    private String email;
}
