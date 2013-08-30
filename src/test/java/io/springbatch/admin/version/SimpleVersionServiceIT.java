package io.springbatch.admin.version;

import static org.junit.Assert.assertNotNull;
import io.springbatch.admin.domain.JobVersion;

import java.io.FileOutputStream;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.AbstractJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/META-INF/spring/batch/hash-job-context.xml",
	"classpath:/META-INF/spring/version/*-context.xml"})
@ActiveProfiles("junit")
public class SimpleVersionServiceIT {

	@Autowired
	private Job job;
	
	@Autowired
	private VersionService versionService;
	
	@Autowired
	private DataSource dataSource;
	
	@After
	public void after() throws Exception {
		//take an export
		IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
		QueryDataSet queryDataSet = new QueryDataSet(connection);
//		queryDataSet.addTable("batch_job_version","select job_name,version,hash_array from batch_job_version");
		queryDataSet.addTable("batch_job_version","select * from batch_job_version");
		FlatXmlDataSet.write(queryDataSet, new FileOutputStream("batch_job_version.xml"));
	}
	
	@Test
	public void test() {
		//load up a job and get a jobVersion for it
		JobVersion jobVersion = versionService.getJobVersion((AbstractJob)job);
		assertNotNull(jobVersion);
		assertNotNull(jobVersion.getId());
	}

}
