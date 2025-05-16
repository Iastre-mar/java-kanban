package ru.yandex.practicum.cva.task.tracker;

import java.util.List;
import java.util.stream.Stream;

public enum ColumnsInFile {
    ID("id"),
    TYPE("type"),
    NAME("name"),
    STATUS("status"),
    DESCRIPTION("description"),
    EPIC("epic"),
    STARTTIME("start_time"),
    DURATION("duration");


    public final String columnName;

    ColumnsInFile(String columnName) {
        this.columnName = columnName;
    }

    public static List<String> getTableHeaderList() {
        return Stream.of(ColumnsInFile.values())
                     .map(ColumnsInFile::getColumnName)
                     .toList();
    }

    public String getColumnName() {
        return columnName;
    }
}
