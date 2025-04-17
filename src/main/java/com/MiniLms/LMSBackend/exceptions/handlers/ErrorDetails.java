package com.MiniLms.LMSBackend.exceptions.handlers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDetails {
    private Date timeStamp;
    private String message;
    private String detils;
}
