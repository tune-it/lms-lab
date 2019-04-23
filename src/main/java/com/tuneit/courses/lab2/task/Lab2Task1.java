package com.tuneit.courses.lab2.task;

import com.tuneit.courses.Task;
import com.tuneit.courses.db.LabTaskQA;
import com.tuneit.courses.db.schema.Column;
import com.tuneit.courses.db.schema.Table;
import com.tuneit.courses.lab2.Lab2Task;
import com.tuneit.courses.lab2.schema.Schema02;
import com.tuneit.courses.lab2.schema.Substring;
import com.tuneit.courses.lab2.schema.TableSubstring;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.tuneit.courses.db.schema.Schema.getRandomElement;

public class Lab2Task1 extends Lab2Task {
    @Override
    public LabTaskQA generate(Schema02 schema02, Task task) {
        query = new StringBuilder();
        answer = new StringBuilder();

        Random random = task.getRandom();


        TableSubstring tableSubstring = getRandomElement(random, schema02.getTablesSubstrings());

        Table table = schema02.findTableBySqlName(tableSubstring.getSqlTableName());

        Substring substringType = getRandomElement(random, tableSubstring.getSubstring());

        Column likeColumn = table.findColumn(substringType.getSqlNameColumn());
        table.getColumns().remove(likeColumn);

        boolean isLeftSubstring = random.nextBoolean();

        String nativeSubstring;
        String sqlSubstring;
        if (isLeftSubstring) {
            String substring = getRandomElement(random, substringType.getLeftSubstrings());
            nativeSubstring = "начинаются на \"" + substring + "\"";
            sqlSubstring = "'" + substring + "%'";
        } else {
            String substring = getRandomElement(random, substringType.getRightSubstrings());
            nativeSubstring = "заканчиваются на \"" + substring + "\"";
            sqlSubstring = "'%" + substring + "'";
        }

        List<Column> columns = table.getRandomColumns(random, 2);
        List<String> columnsRevisedForWrite = new ArrayList<>();
        columns.forEach(
                column -> columnsRevisedForWrite.add(column.getNamePlural()));

        query.append("Выведите содержимое полей ");
        writeColumnToQuery(columnsRevisedForWrite, ", ", query);
        query.append(" из таблицы ")
                .append(table.getNameGenitive())
                .append(", ")
                .append(likeColumn.getNamePlural())
                .append(" которых ")
                .append(nativeSubstring)
                .append(".");

        columnsRevisedForWrite.clear();
        columns.forEach(
                column -> columnsRevisedForWrite.add(column.getColumnName()));

        answer.append("select ");
        writeColumnToQuery(columnsRevisedForWrite, ", ", answer);
        answer.append(" from ")
                .append(table.getTableName())
                .append(" where ")
                .append(likeColumn.getColumnName())
                .append(" like ")
                .append(sqlSubstring);

        return new LabTaskQA(task.getId(), query.toString(), answer.toString());
    }

}