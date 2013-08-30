package io.springbatch.admin.domain;

import org.springframework.data.repository.CrudRepository;

public interface JobVersionRepository extends CrudRepository<JobVersion, Long> {

	JobVersion findByJobName(String jobName);
	
}
