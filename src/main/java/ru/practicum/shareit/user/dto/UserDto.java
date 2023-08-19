package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.interfaces.Create;
import ru.practicum.shareit.interfaces.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    private Long id;
    @NotBlank(groups = {Create.class})
    private String name;
    @Email(groups = {Create.class, Update.class})
    @NotNull(groups = {Create.class}, message = "не должно равняться null")
    private String email;

}
