package org.jacoco.core.analysis;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jacoco.core.JacocoUtil;
import org.jacoco.core.data.ExecutionDataStore;
import org.objectweb.asm.ClassReader;

public class AnalyzerDelegate {
	private Analyzer analyzer;
	private org.jacoco.previous.core.analysis.Analyzer analyzer074;
	
	private final char version;
	
	public AnalyzerDelegate(final ExecutionDataStore executionData,
			final ICoverageVisitor coverageVisitor, final char version) {
		this.version = version;
		
		if (version == JacocoUtil.VERSION_074) {
			analyzer074 = new org.jacoco.previous.core.analysis.Analyzer(executionData, coverageVisitor);
		} else {
			analyzer = new Analyzer(executionData, coverageVisitor);
		}
	}
	
	public void analyzeClass(final ClassReader reader) {
		if (version == JacocoUtil.VERSION_074) {
			analyzer074.analyzeClass(reader);
		} else {
			analyzer.analyzeClass(reader);
		}
	}
	
	public void analyzeClass(final byte[] buffer, final String location)
			throws IOException {
		if (version == JacocoUtil.VERSION_074) {
			analyzer074.analyzeClass(buffer, location);
		} else {
			analyzer.analyzeClass(buffer, location);
		}
	}
	
	public void analyzeClass(final InputStream input, final String location)
			throws IOException {
		if (version == JacocoUtil.VERSION_074) {
			analyzer074.analyzeClass(input, location);
		} else {
			analyzer.analyzeClass(input, location);
		}
	}
	
	public int analyzeAll(final InputStream input, final String location)
			throws IOException {
		if (version == JacocoUtil.VERSION_074) {
			return analyzer074.analyzeAll(input, location);
		} else {
			return analyzer.analyzeAll(input, location);
		}
	}
	
	public int analyzeAll(final File file) throws IOException {
		if (version == JacocoUtil.VERSION_074) {
			return analyzer074.analyzeAll(file);
		} else {
			return analyzer.analyzeAll(file);
		}
	}
	
	public int analyzeAll(final String path, final File basedir)
			throws IOException {
		if (version == JacocoUtil.VERSION_074) {
			return analyzer074.analyzeAll(path, basedir);
		} else {
			return analyzer.analyzeAll(path, basedir);
		}
	}
}
