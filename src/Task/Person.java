package Task;

public class Person {
    String name;
    long totalSales;
    long salesPeriod;
    double experienceMultiplier;

    public Person(String name, long totalSales, long salesPeriod, double experienceMultiplier) {
        this.name = name;
        this.totalSales = totalSales;
        this.salesPeriod = salesPeriod;
        this.experienceMultiplier = experienceMultiplier;
    }

    public String getName() {
        return name;
    }

    public long getTotalSales() {
        return totalSales;
    }

    public long getSalesPeriod() {
        return salesPeriod;
    }

    public double getExperienceMultiplier() {
        return experienceMultiplier;
    }
}

