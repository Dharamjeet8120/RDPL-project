package rdpl_timetable_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rdpl_timetable_backend.loader.SchoolSampleLoader;
import rdpl_timetable_backend.model.SchoolSample;

@RestController
@RequestMapping("/api/timetable")
public class SchoolSampleController {

    private final SchoolSampleLoader schoolSampleLoader;

    public SchoolSampleController(SchoolSampleLoader schoolSampleLoader) {
        this.schoolSampleLoader = schoolSampleLoader;
    }

    @GetMapping("/school-sample")
    public SchoolSample getSchoolSample() {

        return schoolSampleLoader.loadSchoolSample();
    }
}