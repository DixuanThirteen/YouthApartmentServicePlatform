package com.yasp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookRequest {
    private LocalDate startDate; // 计划入住日期
    private Integer leaseMonths; // 计划租期(月)
    private Integer rentCent;
    private Integer depositCent;
}
