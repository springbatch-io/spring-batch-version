package io.springbatch.admin.domain;

import org.springframework.data.repository.CrudRepository;

public interface CrudJobVersionRepository extends CrudRepository<JobVersion, Long> {

	JobVersion findByJobName(String jobName);
	
}
