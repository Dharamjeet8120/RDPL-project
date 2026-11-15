package rdpl_timetable_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rdpl_timetable_backend.loader.SchedulingRulesLoader;
import rdpl_timetable_backend.model.SchedulingRules;

@RestController
@RequestMapping("/api/timetable")
public class SchedulingRulesController {

    private final SchedulingRulesLoader rulesLoader;

    public SchedulingRulesController(SchedulingRulesLoader rulesLoader) {
        this.rulesLoader = rulesLoader;
    }

    @GetMapping("/scheduling-rules")
    public SchedulingRules getSchedulingRules() {

        return rulesLoader.loadSchedulingRules();
    }
}