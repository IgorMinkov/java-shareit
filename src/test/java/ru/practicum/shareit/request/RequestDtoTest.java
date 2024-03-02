package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class RequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestOutDto> json;

    @Test
    void testItemRequestDto() throws Exception {

        LocalDateTime created = LocalDateTime.of(2024, 2, 2, 2, 2);

        ItemDto itemDto = ItemDto.builder()
                .requestId(1L)
                .name("itemName")
                .description("item description")
                .available(true)
                .build();

        ItemRequestOutDto itemRequestOutDto = ItemRequestOutDto.builder()
                .id(1L)
                .description("item request 1")
                .created(created)
                .items(List.of(itemDto))
                .build();

        JsonContent<ItemRequestOutDto> result = json.write(itemRequestOutDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("item request 1");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(created.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }

}
