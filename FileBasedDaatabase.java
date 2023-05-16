import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Column {
    private String name;
    private String dataType;

    public Column(String name, String dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public String getDataType() {
        return dataType;
    }
}

public class FileBasedDatabase {
    private static final String METADATA_FILE = "metadata.csv";

    public static void main(String[] args) {
        String createQuery = "CREATE TABLE my_table (col1 INTEGER, col2 STRING, col3 INTEGER)";
        String insertQuery = "INSERT INTO my_table VALUES (123, 'Hello', 456)";

        executeQuery(createQuery);
        executeQuery(insertQuery);
    }

    public static void executeQuery(String query) {
        if (query.startsWith("CREATE TABLE")) {
            createTable(query);
        } else if (query.startsWith("INSERT INTO")) {
            insertIntoTable(query);
        } else {
            System.out.println("Invalid query");
        }
    }

    public static void createTable(String query) {
        String tableName = "";
        List<Column> columns = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            String columnPart = matcher.group(1);
            String[] columnDefs = columnPart.split(",");
            tableName = query.substring(13, query.indexOf("(")).trim();

            for (String columnDef : columnDefs) {
                String[] columnInfo = columnDef.trim().split("\\s+");
                if (columnInfo.length == 2) {
                    String columnName = columnInfo[0];
                    String dataType = columnInfo[1];
                    columns.add(new Column(columnName, dataType));
                }
            }
        }

        try {
            // Create the metadata file
            BufferedWriter metadataWriter = new BufferedWriter(new FileWriter(METADATA_FILE, true));
            for (Column column : columns) {
                metadataWriter.write(tableName + "," + column.getName() + "," + column.getDataType());
                metadataWriter.newLine();
            }
            metadataWriter.close();

            // Create the table file
            BufferedWriter tableWriter = new BufferedWriter(new FileWriter(tableName + ".csv"));
            tableWriter.close();

            System.out.println("Table created successfully: " + tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void insertIntoTable(String query) {
        String tableName = "";
        List<String> values = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            String valuePart = matcher.group(1);
            String[] valueDefs = valuePart.split(",");

            tableName = query.substring(12, query.indexOf("VALUES")).trim();

            for (String valueDef : valueDefs) {
                values.add(valueDef.trim());
            }
        }

        try {
            // Open the table file in append mode
            BufferedWriter tableWriter = new BufferedWriter(new FileWriter(tableName + ".csv", true));
            tableWriter.write(String.join(",", values));
            tableWriter.newLine();
            tableWriter.close();

            System.out.println("Data inserted into table: " + tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
