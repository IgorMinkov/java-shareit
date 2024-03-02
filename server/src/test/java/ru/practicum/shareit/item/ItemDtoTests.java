package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoTests {

    @Autowired
    private JacksonTester<ItemDto> jsonShort;

    @Autowired
    private JacksonTester<ItemOutDto> jsonBig;

    @Test
    void testItemDto() throws Exception {

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("itemName")
                .description("item description")
                .available(true)
                .requestId(1L)
                .build();

        JsonContent<ItemDto> result = jsonShort.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("itemName");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("item description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    void testItemOutDto() throws Exception {

        ItemOutDto itemOutDto = ItemOutDto.builder()
                .id(1L)
                .name("itemName")
                .description("item description")
                .available(true)
                .requestId(1L)
                .build();

        JsonContent<ItemOutDto> result = jsonBig.write(itemOutDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("itemName");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("item description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

}
