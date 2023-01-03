package com.example.sktest.config.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookDto {
    List<facebookDataDto> data;
    private String message;
    private String picture;
    private String id;
}
