package lk.ijse.gdse.main.internbaybackend.controller;

import lk.ijse.gdse.main.internbaybackend.dto.JobCreateDTO;
import lk.ijse.gdse.main.internbaybackend.dto.ResponsDto;
import lk.ijse.gdse.main.internbaybackend.entity.JobType;
import lk.ijse.gdse.main.internbaybackend.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JobController {

    private final JobService jobService;

    @PostMapping("/create")
    public ResponseEntity<ResponsDto> createJob(@RequestBody JobCreateDTO jobCreateDTO) {
        ResponsDto response = jobService.createJob(jobCreateDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<ResponsDto> getJobById(@PathVariable Long jobId) {
        ResponsDto response = jobService.getJobById(jobId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponsDto> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ResponsDto response = jobService.getAllJobs(page, size);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/employer/{employerId}")
    public ResponseEntity<ResponsDto> getJobsByEmployer(@PathVariable Long employerId) {
        ResponsDto response = jobService.getJobsByEmployer(employerId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponsDto> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) JobType jobType,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        ResponsDto response = jobService.searchJobs(title, categoryId, jobType, 
                                                  minSalary, maxSalary, location, page, size);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<ResponsDto> updateJob(
            @PathVariable Long jobId, 
            @RequestBody JobCreateDTO jobCreateDTO) {
        ResponsDto response = jobService.updateJob(jobId, jobCreateDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<ResponsDto> deleteJob(@PathVariable Long jobId) {
        ResponsDto response = jobService.deleteJob(jobId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/job-types")
    public ResponseEntity<JobType[]> getJobTypes() {
        return ResponseEntity.ok(JobType.values());
    }
}