package Task;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(final String[] args) {

        if (!validateInput(args)) {
            return;
        }

        List<Person> salesPeople = null;
        try {
            salesPeople = retrieveSalesPeopleJson(args[0]);
        } catch (IOException | ParseException e) {
            System.out.println("Problem with opening sales people file.");
            e.printStackTrace();
            return;
        }

        ReportDefinition reportDefinition = null;
        try {
            reportDefinition = retrieveReportDefinitionJson(args[1]);
        } catch (IOException | ParseException e) {
            System.out.println("Problem with opening report definition file.");
            e.printStackTrace();
            return;
        }

        Map<String, Double> bestPerformers = findBestPerformers(salesPeople, reportDefinition);

        try {
            exportResultToCsvFile(bestPerformers);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }


    private static void exportResultToCsvFile(Map<String, Double> bestPerformers) throws IOException {
        if (bestPerformers.isEmpty()) {
            System.out.println("You have set too high standards. Nobody meets them.");
        } else {
            createCSVFile(bestPerformers);
        }
    }

    public static void createCSVFile(Map<String, Double> bestPerformers) throws IOException {

        String filePath = "TopPerformers.csv";

        FileWriter fileWriter = new FileWriter(filePath);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        PrintWriter writeToCsvFile = new PrintWriter(bufferedWriter);

        writeToCsvFile.println("Name, Score");

        bestPerformers
                .entrySet()
                .forEach(entry -> writeToCsvFile.println(entry.getKey() + ", " + entry.getValue()));

        writeToCsvFile.flush();
        writeToCsvFile.close();
    }

    private static Map<String, Double> findBestPerformers(List<Person> salesPeople,
                                                          ReportDefinition reportDefinition) {

        Map<String, Double> bestSalesPeople =
                addAllSalesPeopleWithLowerOrEqualSalesPeriodThanPeriodLimit(salesPeople, reportDefinition);

        bestSalesPeople = sortByScore(bestSalesPeople);

        double theLengthOfEndResult =
                Math.floor(bestSalesPeople.size() * (reportDefinition.getTopPerformersThreshold() * 1.0 / 100));

        if (theLengthOfEndResult == 0) {
            return new HashMap<>();
        } else {
            bestSalesPeople = bestSalesPeople.entrySet()
                    .stream()
                    .limit((long) theLengthOfEndResult)
                    .collect(Collectors.toMap
                            (Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            return bestSalesPeople;
        }


    }

    private static Map<String, Double> addAllSalesPeopleWithLowerOrEqualSalesPeriodThanPeriodLimit
            (List<Person> salesPeople, ReportDefinition reportDefinition) {

        Map<String, Double> bestSalesPeopleOneFilter = new HashMap<>();
        for (Person salesPerson : salesPeople) {
            Person person = salesPerson;

            if (person.getSalesPeriod() <= reportDefinition.getPeriodLimit()) {
                double score = calculateScore(reportDefinition, person);

                if (!bestSalesPeopleOneFilter.containsKey(person.getName())) {
                    bestSalesPeopleOneFilter.put(person.getName(), score);
                } else {
                    System.out.printf
                            ("%s is found more than once. Please enter each person only 1 time!", person.getName());
                }
            }
        }
        return bestSalesPeopleOneFilter;
    }


    public static Map<String, Double> sortByScore(final Map<String, Double> wordCounts) {

        return wordCounts.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Double>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private static double calculateScore(ReportDefinition reportDefinition, Person person) {
        double score;
        if (reportDefinition.isUseExprienceMultiplier()) {
            score = (person.totalSales * 1.0 / person.salesPeriod) * person.experienceMultiplier;
        } else {
            score = (person.totalSales * 1.0 / person.salesPeriod);
        }
        return score;
    }

    private static ReportDefinition retrieveReportDefinitionJson(String filePath) throws IOException, ParseException {
        ReportDefinition reReportDefinition = null;

        JSONParser jsonP = new JSONParser();

        FileReader reader = new FileReader(filePath);
        Object obj = jsonP.parse(reader);

        JSONObject jsonObject = (JSONObject) obj;

        long topPerformerThreshold = (long) jsonObject.get("topPerformersThreshold");
        boolean useExprienceMultiplier = (boolean) jsonObject.get("useExprienceMultiplier");
        long periodLimit = (long) jsonObject.get("periodLimit");

        reReportDefinition = new ReportDefinition(topPerformerThreshold, useExprienceMultiplier, periodLimit);

        return reReportDefinition;
    }

    private static List<Person> retrieveSalesPeopleJson(String filePath) throws IOException, ParseException {
        JSONParser jsonP = new JSONParser();

        List<Person> salesPeople = new ArrayList<>();

        FileReader reader = new FileReader(filePath);

        Object obj = jsonP.parse(reader);

        JSONArray array = (JSONArray) obj;

        for (int i = 0; i < array.size(); i++) {
            Object o = array.get(i);
            JSONObject jsonObject = (JSONObject) o;

            String name = (String) jsonObject.get("name");
            long totalSales = (long) jsonObject.get("totalSales");
            long salesPeriod = (long) jsonObject.get("salesPeriod");
            double experienceMultiplier = (double) jsonObject.get("experienceMultiplier");

            Person person = new Person(name, totalSales, salesPeriod, experienceMultiplier);
            salesPeople.add(person);
        }

        return salesPeople;
    }

    private static boolean validateInput(String[] args) {
        boolean validInput = true;

        String pathToSalesPeople = null;
        try {
            pathToSalesPeople = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Please type 2 arguments.");
            return false;
        }

        if (!pathToSalesPeople.substring(pathToSalesPeople.length() - 5).equals(".json")) {
            validInput = false;
            System.out.println("The sales people file you've typed is not in .json format");
        }

        String pathToReportDefinition = null;

        try {
            pathToReportDefinition = args[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Please type a second argument as well.");
            return false;
        }

        if (!pathToReportDefinition.substring(pathToReportDefinition.length() - 5).equals(".json")) {
            validInput = false;
            System.out.println("The report definition file you have typed is not in .json format");
        }
        return validInput;
    }
}
