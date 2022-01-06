package com.fz.Entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Person {
    private String name;
    private String idCard;
    private String phone;
    private String type;
}
