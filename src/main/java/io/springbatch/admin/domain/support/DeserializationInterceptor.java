package io.springbatch.admin.domain.support;

import io.springbatch.admin.domain.JobVersion;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.serializer.DefaultDeserializer;
import org.springframework.core.serializer.Deserializer;

public class DeserializationInterceptor implements MethodInterceptor, InitializingBean {

	@SuppressWarnings("rawtypes")
	private Deserializer deserializer;
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		//invoke
		Object result = invocation.proceed();
		//deserialize the result
		if (result instanceof Iterable) {
			for (Object object : (Iterable)result) {
				deserialize(object);
			}//end for
		} else {
			deserialize(result);
		}//end if
		//return
		return result;
	}

	@SuppressWarnings("unchecked")
	protected void deserialize(Object object) throws Exception {
		if (object instanceof JobVersion
				&& ((JobVersion)object).getHashArray() != null) {
			ByteArrayInputStream bis = new ByteArrayInputStream(((JobVersion)object).getHashArray());
			Map<String,List<Integer>> map = (Map<String, List<Integer>>) deserializer.deserialize(bis);
			//set
			((JobVersion)object).setBeanHash(map);
		}//end if
	}
	
	
	@SuppressWarnings("rawtypes")
	public void setDeserializer(Deserializer deserializer) {
		this.deserializer = deserializer;
	}



	@Override
	public void afterPropertiesSet() throws Exception {
		if (deserializer == null) {
			deserializer = new DefaultDeserializer();
		}//end if
	}

}
