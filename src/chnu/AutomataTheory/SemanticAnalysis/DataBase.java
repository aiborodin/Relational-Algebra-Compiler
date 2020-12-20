package chnu.AutomataTheory.SemanticAnalysis;

import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class DataBase {

    private final Map<String, Table> tablesMap;

    private static final String DB_URL = "jdbc:sqlite:doctors.db";

    public static void main(String[] args) {
        new DataBase();
    }

    public DataBase() {
        this.tablesMap = new HashMap<>();
        try (Connection conn = getConnection()) {
            DatabaseMetaData md = conn.getMetaData();
            try (ResultSet tablesSet = md.getTables(null, null, "%", null)) {
                String tableName;
                while (tablesSet.next()) {
                    tableName = tablesSet.getString(3);
                    Table table = new Table(tableName);
                    this.tablesMap.put(tableName, table);
                    try (ResultSet columnsSet = md.getColumns(null, null, tableName, null)) {
                        Column column;
                        while (columnsSet.next()) {
                            String name = columnsSet.getString("COLUMN_NAME");
                            String type = columnsSet.getString("TYPE_NAME");
                            //int size = columnsSet.getInt("COLUMN_SIZE");
                            column = new Column(name);
                            column.addAttribute("type", type);
                            table.addColumn(column);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            connection = DriverManager.getConnection(DB_URL, config.toProperties());
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return connection;
    }

    public boolean hasTable(String tableName) {
        return tablesMap.containsKey(tableName);
    }

    public boolean checkColumn(String tableName, String columnName) {
        if (tablesMap.containsKey(tableName)) {
            return tablesMap.get(tableName).hasColumn(columnName);
        }
        return false;
    }

    public boolean checkMinusCompatibility(String leftTable, String rightTable) {
        Table left = tablesMap.get(leftTable);
        Table right = tablesMap.get(rightTable);
        for (String leftColumnName : left.columnNames()) {
            if (!right.hasColumn(leftColumnName)) {
                return false;
            }
        }
        return true;
    }

    public String checkJoinCompatibility(String leftTable, String rightTable, String leftColumn, String rightColumn) {
        if (!checkColumn(leftTable, leftColumn)) {
            return "There is no column '" + leftColumn + "' in table '" + leftTable + "'.";
        }
        if (!checkColumn(rightTable, rightColumn)) {
            return "There is no column '" + rightColumn + "' in table '" + rightTable + "'.";
        }
        Table left = tablesMap.get(leftTable);
        Table right = tablesMap.get(rightTable);
        String leftType = (String)left.getColumn(leftColumn).getAttribute("type");
        String rightType = (String)right.getColumn(rightColumn).getAttribute("type");
        if (!leftType.equals(rightType)) {
            return leftTable + "." + leftColumn + "' of type '" + leftType + "' and '" + rightTable + "." + rightColumn + "' of type '" + rightType + "' " +
                    "are not compatible types for left join operation.";
        }
        return null;
    }
}

class Table {

    String name;
    Map<String, Column> columnsMap;

    public Table(String name) {
        this.name = name;
        columnsMap = new HashMap<>();
    }

    public Set<String> columnNames() {
        return columnsMap.keySet();
    }

    public Column getColumn(String name) {
        return columnsMap.get(name);
    }

    public void addColumn(Column column) {
        columnsMap.put(column.getName(), column);
    }

    public boolean hasColumn(String columnName) {
        return columnsMap.containsKey(columnName);
    }
}

class Column {

    private final String name;
    private final Map<String, Object> attributes;

    public Column(String name) {
        this.name = name;
        attributes = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void addAttribute(String attrName, Object value) {
        attributes.put(attrName, value);
    }

    public Object getAttribute(String attrName) {
        return attributes.get(attrName);
    }
}