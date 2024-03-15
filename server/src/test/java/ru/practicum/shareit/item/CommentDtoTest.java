package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentOutDto> json;

    @Test
    void testCommentOutDto() throws Exception {

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Alex")
                .email("alexFirst@yandex.ru")
                .build();

        CommentOutDto commentDto = CommentOutDto.builder()
                .id(1L)
                .text("first!")
                .created(LocalDateTime.now())
                .authorName(userDto.getName())
                .build();

        JsonContent<CommentOutDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("first!");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Alex");
    }

}
