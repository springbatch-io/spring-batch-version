package io.springbatch.admin.version;

import static org.junit.Assert.assertFalse;

import java.io.FileInputStream;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
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
public class VariationSimpleVersionServiceIT {

	@Autowired
	private Job job;
	
	@Autowired
	private VersionService versionService;
	
	@Autowired
	private DataSource dataSource;
	
	@Before
	public void before() throws Exception {
		//load up the dataset
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		IDataSet dataSet = builder.build(new FileInputStream("batch_job_version.xml"));
		IDatabaseConnection dataConnection = new DatabaseConnection(dataSource.getConnection());
		DatabaseOperation.CLEAN_INSERT.execute(dataConnection, dataSet);
	}
	
	@Test
	public void test() {
		//load up a job and get a jobVersion for it
		assertFalse(versionService.hasChanged((AbstractJob) job, null));
	}

}
