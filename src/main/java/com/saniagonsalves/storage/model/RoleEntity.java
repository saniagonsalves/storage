package com.saniagonsalves.storage.model;

import lombok.AllArgsConstructor;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@AllArgsConstructor
public enum RoleEntity {

    ADMIN("admin"),
    USER("user");

    public final String sqlValue;

    @Converter
    public static class Mapper implements AttributeConverter<RoleEntity, String> {

        @Override
        public String convertToDatabaseColumn(RoleEntity attribute) {
            return attribute.sqlValue;
        }

        @Override
        public RoleEntity convertToEntityAttribute(String value) {
            switch (value) {
                case "admin":
                    return ADMIN;
                case "user":
                    return USER;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }
}
