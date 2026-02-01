package org.example;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.List;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) {
        List<Employee> list = parseXML("data.xml");
        String json = listToJson(list);
        writeString(json, "data2.json");
    }


    public static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(fileName));

            Node root = document.getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    long id = Long.parseLong(
                            element.getElementsByTagName("id").item(0).getTextContent()
                    );
                    String firstName =
                            element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName =
                            element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country =
                            element.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(
                            element.getElementsByTagName("age").item(0).getTextContent()
                    );

                    Employee employee = new Employee(id, firstName, lastName, country, age);
                    employees.add(employee);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }


    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {

            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            return csvToBean.parse();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String listToJson(List<Employee> list) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
