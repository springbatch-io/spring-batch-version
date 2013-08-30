package io.springbatch.admin.domain.support;

import io.springbatch.admin.domain.JobVersion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.serializer.DefaultSerializer;
import org.springframework.core.serializer.Serializer;

public class SerializationInterceptor implements MethodInterceptor, InitializingBean {

	@SuppressWarnings("rawtypes")
	private Serializer serializer;
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		//serialize the object before saving
		Object argument = invocation.getArguments()[0];
		//check type
		if (argument instanceof Iterable) {
			//loop and serialize
			for (Object object : (List<?>)argument) {
				serialize(object);
			}//end for
		} else {
			//serialize instance
			serialize(argument);
		}//end if
		//proceed
		return invocation.proceed();
	}

	@SuppressWarnings("unchecked")
	protected void serialize(Object object) throws IOException {
		if (object instanceof JobVersion) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			serializer.serialize(((JobVersion)object).getBeanHash(), bos);
			//set
			((JobVersion)object).setHashArray(bos.toByteArray());
		}//end if
	}
	
	
	@SuppressWarnings("rawtypes")
	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (serializer == null) {
			serializer = new DefaultSerializer();
		}//end if
	}

}
