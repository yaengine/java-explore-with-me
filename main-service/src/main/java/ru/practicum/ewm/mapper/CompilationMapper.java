package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Compilation;

@Component
public class CompilationMapper {

    public CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .events(EventMapper.toEventShortDtoSet(compilation.getEvents()))
                .pinned(compilation.isPinned())
                .build();
    }

    public Category toCategory(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

    public Category toCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }
}
