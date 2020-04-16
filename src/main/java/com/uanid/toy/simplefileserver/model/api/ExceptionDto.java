package com.uanid.toy.simplefileserver.model.api;

import lombok.*;

/**
 * @author uanid
 * @since 2020-04-16
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExceptionDto {
    private String developerMessage;
    private String error;
    private String errorMessage;
}
