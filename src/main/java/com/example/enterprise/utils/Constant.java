package com.example.enterprise.utils;

import java.text.DecimalFormat;

import org.springframework.stereotype.Service;

public class Constant {

    public enum ProjectId {
        ASSET_PROJECT_ID("ERP-PRO-001"),
        NERAM_PROJECT_ID("ERP-PRO-002");

        private final String data;

        ProjectId(String data) {
            this.data = data;
        }

        public String getData() {
            return this.data;
        }
    }
}
