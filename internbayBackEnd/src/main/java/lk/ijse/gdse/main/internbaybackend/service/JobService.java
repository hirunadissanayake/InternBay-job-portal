package lk.ijse.gdse.main.internbaybackend.service;

import lk.ijse.gdse.main.internbaybackend.dto.JobCreateDTO;
import lk.ijse.gdse.main.internbaybackend.dto.JobResponseDTO;
import lk.ijse.gdse.main.internbaybackend.dto.ResponsDto;
import lk.ijse.gdse.main.internbaybackend.entity.JobType;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface JobService {
    ResponsDto createJob(JobCreateDTO jobCreateDTO);
    ResponsDto getJobById(Long jobId);
    ResponsDto getAllJobs(int page, int size);
    ResponsDto getJobsByEmployer(Long employerId);
    ResponsDto searchJobs(String title, Long categoryId, JobType jobType, 
                          BigDecimal minSalary, BigDecimal maxSalary, 
                          String location, int page, int size);
    ResponsDto updateJob(Long jobId, JobCreateDTO jobCreateDTO);
    ResponsDto deleteJob(Long jobId);
}