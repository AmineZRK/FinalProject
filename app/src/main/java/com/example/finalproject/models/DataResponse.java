package com.example.finalproject.models;

import java.util.List;

public class DataResponse {
        private int code;
        private String message;
        private List<Waste> data;

        public int getCode() {
                return code;
        }

        public void setCode(int code) {
                this.code = code;
        }

        public String getMessage() {
                return message;
        }

        public void setMessage(String message) {
                this.message = message;
        }

        public List<Waste> getData() {
                return data;
        }

        public void setData(List<Waste> data) {
                this.data = data;
        }
// getters and setters


}
