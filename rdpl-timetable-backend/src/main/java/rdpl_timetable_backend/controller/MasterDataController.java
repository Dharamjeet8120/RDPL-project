package rdpl_timetable_backend.controller;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import rdpl_timetable_backend.service.MasterDataService;
@RestController @RequestMapping("/api/timetable/data")
public class MasterDataController {
 private final MasterDataService service;
 public MasterDataController(MasterDataService service){this.service=service;}
 @GetMapping("/status") public Map<String,Object> status(){return service.status();}
 /** Replaces current master records with the original bundled JSON baseline. */
 @PostMapping("/import") public Map<String,Object> importData(){return service.importJsonReplacingAll();}
}
