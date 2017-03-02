package org.jacoco.core;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.jacoco.core.data.ExecutionDataWriter;

public class JacocoUtil {
	public static final char VERSION_074 = 0x1006;
	
	public static char getVersion(File jacocoExec) throws IOException {
		char version;
		
		try (DataInputStream in = new DataInputStream(new FileInputStream(jacocoExec))) {
			if (in.read() != ExecutionDataWriter.BLOCK_HEADER) {
				throw new IOException("Invalid execution data file.");
			}
			
			if (in.readChar() != ExecutionDataWriter.MAGIC_NUMBER) {
				throw new IOException("Invalid execution data file.");
			}
			
			version = in.readChar();
		}
		
		return version;
	}
}
