package ru.ufa;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");

        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list2);
        writeString(json2, "data2.json");
    }

    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> employeeList = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        Node root = doc.getDocumentElement();
        //System.out.println("Корневой элемент: " + root.getNodeName());
        //read(root);
        doc.getDocumentElement().normalize();
        NodeList nodeEmployeeList = doc.getElementsByTagName("employee");
        for (int i = 0; i < nodeEmployeeList.getLength(); i++) {
            Node node = nodeEmployeeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                NodeList nodeList1 = element.getElementsByTagName("id");
                Element element1 = (Element) nodeList1.item(0);
                NodeList nodeList1_ = element1.getChildNodes();
                String id = ((Node) nodeList1_.item(0)).getNodeValue();

                NodeList nodeList2 = element.getElementsByTagName("firstName");
                Element element2 = (Element) nodeList2.item(0);
                NodeList nodeList2_ = element2.getChildNodes();
                String firstName = ((Node) nodeList2_.item(0)).getNodeValue();

                NodeList nodeList3 = element.getElementsByTagName("lastName");
                Element element3 = (Element) nodeList3.item(0);
                NodeList nodeList3_ = element3.getChildNodes();
                String lastName = ((Node) nodeList3_.item(0)).getNodeValue();

                NodeList nodeList4 = element.getElementsByTagName("country");
                Element element4 = (Element) nodeList4.item(0);
                NodeList nodeList4_ = element4.getChildNodes();
                String country = ((Node) nodeList4_.item(0)).getNodeValue();

                NodeList nodeList5 = element.getElementsByTagName("age");
                Element element5 = (Element) nodeList5.item(0);
                NodeList nodeList5_ = element5.getChildNodes();
                String age = ((Node) nodeList5_.item(0)).getNodeValue();

                Employee employee = new Employee(Long.parseLong(id), firstName, lastName, country, Integer.parseInt(age));
                employeeList.add(employee);
            }
        }
        return employeeList;
    }

//    private static void read(Node node) {
//        NodeList nodeList = node.getChildNodes();
//        for (int i = 0; i < nodeList.getLength(); i++) {
//            Node node_ = nodeList.item(i);
//            if (Node.ELEMENT_NODE == node_.getNodeType()) {
//                Element element = (Element) node_;
//                NamedNodeMap map = element.getAttributes();
//                for (int j = 0; j < map.getLength(); j++) {
//                    String attrName = map.item(j).getNodeName();
//                    String attrValue = map.item(j).getNodeValue();
//                    System.out.println("Атрибут: " + attrName + "; значение " + attrValue);
//                }
//                read(node_);
//            }
//        }
//    }

    private static void writeString(String json, String jsonFile) {
        try (FileWriter file = new FileWriter(jsonFile)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> employeeList = csvToBean.parse();
            return employeeList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}