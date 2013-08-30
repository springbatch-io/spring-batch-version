package io.springbatch.admin.version;

import io.springbatch.admin.domain.CrudJobVersionRepository;
import io.springbatch.admin.domain.JobVersion;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.AbstractJob;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import com.thoughtworks.xstream.XStream;

public class SimpleVersionService implements VersionService, ApplicationContextAware {

	private XStream xstream = new XStream();
	
	private ConfigurableApplicationContext applicationContext;
	
	@Autowired
	private CrudJobVersionRepository jobVersionRepository;

	@Override
	public JobVersion getJobVersion(AbstractJob job) {
		return getJobVersion(job, Boolean.TRUE);
	}
	
	protected JobVersion getJobVersion(AbstractJob job,boolean persist) {
		//create a job version
		JobVersion jobVersion = new JobVersion();
		jobVersion.setJobName(job.getName());
		//hash the general configuration
//		jobVersion.getBeanHash().put(job.getName(), Arrays.asList(Arrays.hashCode(xstream.toXML(job).getBytes())));
		jobVersion.getBeanHash().put(job.getName(), Arrays.asList(this.hashStepFlow(job)));
		//now hash the classes 'behind' the steps
		for (String stepName : job.getStepNames()) {
			List<Integer> integers = new ArrayList<Integer>();
			for (PropertyValue value : applicationContext.getBeanFactory().getBeanDefinition(stepName).getPropertyValues().getPropertyValues()) {		
				if (value.getValue() instanceof RuntimeBeanReference) {
					String beanName = ((RuntimeBeanReference) value.getValue()).getBeanName();
					Object bean = applicationContext.getBean(beanName);
					if (isType(bean)) {
						//get the class name
						try {
							integers.add(getBeanHash(beanName));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}//end if
				}//end if
			}//end for
			jobVersion.getBeanHash().put(stepName, integers);
		}//end for
		jobVersion.setVersion(1);//version 1
		//save
		if (persist) {
			jobVersionRepository.save(jobVersion);
		}//end if
		//return
		return jobVersion;
	}

	@Override
	public boolean hasChanged(AbstractJob job, JobVersion jobVersion) {
		if (jobVersion == null) {
			//get the latest from the repo for the job
			jobVersion = jobVersionRepository.findByJobName(job.getName());
		}//end if
		//hash the job first and check
		//TODO - always going to be different due to hashing repos' etc.
		Integer jobInteger = hashStepFlow(job);
		//check
		List<Integer> existing = jobVersion.getBeanHash().get(job.getName());
		for (Integer existingVersion : existing) {
			if (!existingVersion.equals(jobInteger)) {
				return true;//immediately exit --> job definition doesn't match
			}//end if
		}//end for
		//now need to check the steps --> load the full jobversion
		JobVersion loadedVersion = getJobVersion(job,Boolean.FALSE);
		//compare
		return equals(loadedVersion,jobVersion);
	}

	private boolean equals(JobVersion current,JobVersion persistent) {
		//get the bean hash and go through
		for (Entry<String,List<Integer>> entry : current.getBeanHash().entrySet()) {
			//retrieve the value from the persistent
			List<Integer> persistentHashes = persistent.getBeanHash().get(entry.getKey());
			//loop
			for (Integer currentHash : entry.getValue()) {
				if (!persistentHashes.contains(currentHash)) {
					//no match
					return false;
				}//end if
			}//end for
		}//end for
		//default
		return true;
	}
	
	private Integer hashStepFlow(AbstractJob job) {
		//build up an array of strings -> hash
		List<String> names = new ArrayList<String>();
		//add the job name
		names.add(job.getName());
		//loop
		for (String stepName : job.getStepNames()) {
			names.add(stepName);
		}//end for
		//return hash
		return Arrays.hashCode(names.toArray());
	}
	
	private boolean isType(Object bean) {
		if (bean instanceof Tasklet
				|| bean instanceof Step
				|| bean instanceof ItemReader
				|| bean instanceof ItemProcessor
				|| bean instanceof ItemWriter
				|| bean instanceof Job
				|| bean instanceof Partitioner) {
			return true;//can process
		} else {
			return false;//don't process
		}//end if		
	}
	
	private Integer getBeanHash(String beanName) throws Exception {
		String name = File.separator + applicationContext.getBeanFactory().getBeanDefinition(beanName).getBeanClassName().replace('.', File.separatorChar) + ".class";
		InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream(name));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int i = 0;
		while ((i = reader.read()) > -1) {
			bos.write(i);
		}//end while
		reader.close();
		bos.flush();
		bos.close();		
		return Arrays.hashCode(bos.toByteArray());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = (ConfigurableApplicationContext) applicationContext;
	}
	
}
