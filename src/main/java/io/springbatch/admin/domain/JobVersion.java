package io.springbatch.admin.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="batch_job_version")
public class JobVersion implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="job_name")
	private String jobName;
	
	@Column(name="version")
	private Integer version;

	@Lob
	@Column(name="hash_array",length=Integer.MAX_VALUE)
	private byte[] hashArray;
	
	@Transient
	private Map<String,List<Integer>> beanHash;

	public JobVersion() {
		beanHash = new HashMap<String,List<Integer>>();
	}
	
	public JobVersion(String jobName) {
		this();
		this.jobName = jobName;
	}
	
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public byte[] getHashArray() {
		return hashArray;
	}

	public void setHashArray(byte[] hashArray) {
		this.hashArray = hashArray;
	}

	public Map<String, List<Integer>> getBeanHash() {
		return beanHash;
	}

	public void setBeanHash(Map<String, List<Integer>> beanHash) {
		this.beanHash = beanHash;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((beanHash == null) ? 0 : beanHash.hashCode());
		result = prime * result + Arrays.hashCode(hashArray);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((jobName == null) ? 0 : jobName.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobVersion other = (JobVersion) obj;
		if (beanHash == null) {
			if (other.beanHash != null)
				return false;
		} else if (!beanHash.equals(other.beanHash))
			return false;
		if (!Arrays.equals(hashArray, other.hashArray))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (jobName == null) {
			if (other.jobName != null)
				return false;
		} else if (!jobName.equals(other.jobName))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}




}
