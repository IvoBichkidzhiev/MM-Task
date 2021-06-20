package Task;

public class ReportDefinition {
    private final long topPerformersThreshold;
    private final boolean useExprienceMultiplier;
    private final long periodLimit;

    public ReportDefinition(long topPerformersThreshold, boolean useExprienceMultiplier, long periodLimit) {
        this.topPerformersThreshold = topPerformersThreshold;
        this.useExprienceMultiplier = useExprienceMultiplier;
        this.periodLimit = periodLimit;
    }

    public long getTopPerformersThreshold() {
        return topPerformersThreshold;
    }

    public boolean isUseExprienceMultiplier() {
        return useExprienceMultiplier;
    }

    public long getPeriodLimit() {
        return periodLimit;
    }
}
