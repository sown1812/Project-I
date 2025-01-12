package com.example.project1;

// Lớp để lưu trữ từ khóa, các dòng xuất hiện và số lần xuất hiện
public class KeywordEntry {
    private String keyword;
    private String lines;
    private int occurrences;

    public KeywordEntry(String keyword, String lines, int occurrences) {
        this.keyword = keyword;
        this.lines = lines;
        this.occurrences = occurrences;
    }


    public String getKeyword() { return keyword; }
    public String getLines() { return lines; }
    public int getOccurrences() { return occurrences; }
}