package com.dexesttp.hkxpack.cli.commands;

import java.io.File;

import com.dexesttp.hkxpack.cli.utils.CLIProperties;
import com.dexesttp.hkxpack.data.HKXFile;
import com.dexesttp.hkxpack.descriptor.HKXDescriptorFactory;
import com.dexesttp.hkxpack.descriptor.HKXEnumResolver;
import com.dexesttp.hkxpack.hkxreader.HKXReader;
import com.dexesttp.hkxpack.tagwriter.TagXMLWriter;

public class Command_unpack extends Command_IO {

	@Override
	protected Runnable getThreadLambda(String inputFileName, String outputFileName,
			HKXDescriptorFactory descriptorFactory, HKXEnumResolver enumResolver) {
		return () -> {
			try {
				// Read file
				File inFile = new File(inputFileName);
				HKXReader reader = new HKXReader(inFile, descriptorFactory, enumResolver);
				HKXFile hkxFile = reader.read();
				
				// Write file
				File outFile = new File(outputFileName);
				TagXMLWriter writer = new TagXMLWriter(outFile);
				writer.write(hkxFile);
			} catch (Exception e) {
				System.out.println("Error reading file : " + inputFileName);
				if(CLIProperties.debug)
					e.printStackTrace();
				else if(!CLIProperties.quiet)
					System.err.println(e.getMessage());
			} finally {
				if(CLIProperties.verbose) {
					System.out.println(inputFileName);
					System.out.println("\t=> " + outputFileName);
				}
			}
		};
	}

	@Override
	protected String extractFileName(String ogName) {
		return ogName.substring(0, ogName.lastIndexOf(".")) + ".xml";
	}

	@Override
	protected String[] getFileExtensions() {
		return new String[] {".hkx"};
	}
}
