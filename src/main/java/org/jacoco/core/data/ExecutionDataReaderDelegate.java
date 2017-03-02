package org.jacoco.core.data;

import java.io.IOException;
import java.io.InputStream;

import org.jacoco.core.JacocoUtil;

public class ExecutionDataReaderDelegate {
	private ExecutionDataReader reader;
	private org.jacoco.previous.core.data.ExecutionDataReader reader074;
	
	private final char version;
	
	public ExecutionDataReaderDelegate(final InputStream input, final char version) throws IOException {
		this.version = version;
		
		if (version == JacocoUtil.VERSION_074) {
			reader074 = new org.jacoco.previous.core.data.ExecutionDataReader(input);
		} else {
			reader = new ExecutionDataReader(input);
		}
	}
	
	public void setSessionInfoVisitor(final ISessionInfoVisitor visitor) {
		if (version == JacocoUtil.VERSION_074) {
			reader074.setSessionInfoVisitor(visitor);
		} else {
			reader.setSessionInfoVisitor(visitor);
		}
	}
	
	public void setExecutionDataVisitor(final IExecutionDataVisitor visitor) {
		if (version == JacocoUtil.VERSION_074) {
			reader074.setExecutionDataVisitor(visitor);
		} else {
			reader.setExecutionDataVisitor(visitor);
		}
	}
	
	public boolean read() throws IOException,
		IncompatibleExecDataVersionException {
		
		if (version == JacocoUtil.VERSION_074) {
			return reader074.read();
		} else {
			return reader.read();
		}
	}
}
