package io.springbatch.admin.version;

import io.springbatch.admin.domain.JobVersion;

import org.springframework.batch.core.job.AbstractJob;

/**
 * return a job version object from a job
 * @author wschipp
 *
 */
public interface VersionService {

	public JobVersion getJobVersion(AbstractJob job);
	
	public boolean hasChanged(AbstractJob job,JobVersion jobVersion);
	
}
