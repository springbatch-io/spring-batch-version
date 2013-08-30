package io.springbatch.admin.version;

import io.springbatch.admin.domain.JobVersion;
import io.springbatch.admin.domain.JobVersionRepository;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.AbstractJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class JobVersionRefreshed implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private JobVersionRepository jobVersionRepository;
	
	@Autowired
	private VersionService versionService;
	
	private ApplicationContext context;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		//load the application context
		context = event.getApplicationContext();
		//get any and all jobs
		Map<String,Job> jobs = context.getBeansOfType(Job.class);
		for (Job job : jobs.values()) {
			//retrieve any jobversions for the job name
			JobVersion jobVersion = jobVersionRepository.findByJobName(job.getName());
			if (jobVersion != null) {
				//compare
				versionService.hasChanged((AbstractJob)job, jobVersion);
			} else {
				//it's new 
				versionService.getJobVersion((AbstractJob)job);
			}//end if
		}//end for
		//at the end of this process, i should have a series of job definitions
	}

}
