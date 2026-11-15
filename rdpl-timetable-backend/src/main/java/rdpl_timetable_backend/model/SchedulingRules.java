package rdpl_timetable_backend.model;

import java.util.List;
import java.util.Map;

public class SchedulingRules {

    private List<Map<String, Object>> mandatoryHard;
    private List<Map<String, Object>> blockAndLayout;
    private List<Map<String, Object>> soft;
    private List<Map<String, Object>> optional;
    private List<Map<String, Object>> deferred;

    public SchedulingRules() {
    }

    public List<Map<String, Object>> getMandatoryHard() {
        return mandatoryHard;
    }

    public void setMandatoryHard(List<Map<String, Object>> mandatoryHard) {
        this.mandatoryHard = mandatoryHard;
    }

    public List<Map<String, Object>> getBlockAndLayout() {
        return blockAndLayout;
    }

    public void setBlockAndLayout(List<Map<String, Object>> blockAndLayout) {
        this.blockAndLayout = blockAndLayout;
    }

    public List<Map<String, Object>> getSoft() {
        return soft;
    }

    public void setSoft(List<Map<String, Object>> soft) {
        this.soft = soft;
    }

    public List<Map<String, Object>> getOptional() {
        return optional;
    }

    public void setOptional(List<Map<String, Object>> optional) {
        this.optional = optional;
    }

    public List<Map<String, Object>> getDeferred() {
        return deferred;
    }

    public void setDeferred(List<Map<String, Object>> deferred) {
        this.deferred = deferred;
    }
}