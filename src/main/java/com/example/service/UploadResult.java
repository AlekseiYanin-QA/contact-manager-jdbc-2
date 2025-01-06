package com.example.service;

public class UploadResult {
    private final int uniqueCount;
    private final int duplicateCount;

    public UploadResult(int uniqueCount, int duplicateCount) {
        this.uniqueCount = uniqueCount;
        this.duplicateCount = duplicateCount;
    }

    public int getUniqueCount() {
        return uniqueCount;
    }

    public int getDuplicateCount() {
        return duplicateCount;
    }
}
