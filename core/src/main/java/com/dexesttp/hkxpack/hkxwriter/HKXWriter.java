package com.dexesttp.hkxpack.hkxwriter;

import java.io.File;
import java.io.IOException;

import com.dexesttp.hkxpack.data.HKXFile;
import com.dexesttp.hkxpack.descriptor.HKXEnumResolver;
import com.dexesttp.hkxpack.hkx.classnames.ClassnamesData;
import com.dexesttp.hkxpack.hkx.exceptions.UnsupportedVersionError;
import com.dexesttp.hkxpack.hkx.header.HeaderData;
import com.dexesttp.hkxpack.hkx.header.SectionData;
import com.dexesttp.hkxpack.hkxreader.HKXReader;
import com.dexesttp.hkxpack.hkxwriter.classnames.HKXClassnamesHandler;
import com.dexesttp.hkxpack.hkxwriter.exceptions.WrongInputCastException;
import com.dexesttp.hkxpack.hkxwriter.header.HKXHeaderFactory;
import com.dexesttp.hkxpack.hkxwriter.header.HKXSectionHandler;
import com.dexesttp.hkxpack.hkxwriter.utils.PointerResolver;
import com.dexesttp.hkxpack.l10n.SBundle;
import com.dexesttp.hkxpack.resources.LoggerUtil;

/**
 * Handles writing a {@link HKXFile} into a {@link File}, using the binary hkx notation.
 */
public class HKXWriter {
	private HKXEnumResolver enumResolver;
	private File outputFile;

	/**
	 * Creates a {@link HKXWriter}.
	 * @param outputFile the {@link File} to output data into.
	 * @param enumResolver the {@link HKXEnumResolver} to use.
	 */
	public HKXWriter(File outputFile, HKXEnumResolver enumResolver) {
		this.enumResolver = enumResolver;
		this.outputFile = outputFile;
	}
	
	/**
	 * Writes a {@link HKXFile}'s data inot this {@link HKXReader}'s {@link File}.
	 * @param file the {@link HKXFile} to take data from.
	 * @throws IOException 
	 * @throws UnsupportedVersionError 
	 */
	public void write(HKXFile file) throws IOException, UnsupportedVersionError {
		// Connect to the file.
		HKXWriterConnector connector = new HKXWriterConnector(outputFile);
		connector.clean();
		
		// Create the header.
		HeaderData header = new HKXHeaderFactory().create(file);
		
		// Create the file's section data.
		HKXSectionHandler sectionHandler = new HKXSectionHandler(header);
		SectionData classnames = new SectionData();
		SectionData types = new SectionData();
		SectionData data = new SectionData();
		sectionHandler.init(HKXSectionHandler.CLASSNAME, classnames);
		
		// Create the ClassNames data.
		HKXClassnamesHandler cnameHandler = new HKXClassnamesHandler();
		ClassnamesData cnameData = cnameHandler.getClassnames(file);
		
		// Write ClassNames data to the file.
		long classnamesEnd = connector.writeClassnames(classnames, cnameData);
		sectionHandler.fillCName(classnames, classnamesEnd);
		connector.writeHeader(header);
		connector.writeSection(header, HKXSectionHandler.CLASSNAME, classnames);
		
		// Update things to prepare for Data writing.
		sectionHandler.init(HKXSectionHandler.TYPES, types);
		connector.writeSection(header, HKXSectionHandler.TYPES, types);
		sectionHandler.init(HKXSectionHandler.DATA, data);
		
		// Write data in the file and store data1/data2/data3 values.
		PointerResolver resolver = new PointerResolver();
		HKXDataHandler dataHandler = new HKXDataHandler(outputFile, cnameData, enumResolver);
		try {
			long endData = dataHandler.fillFile(data, file, resolver) - data.offset;
			data.data1 = endData%0x10 == 0 ? endData : (1 + endData / 0x10) * 0x10;
			dataHandler.fillPointers(data, resolver);
			
			// Write the data section to the file.
			connector.writeSection(header, HKXSectionHandler.DATA, data);
		} catch(ClassCastException e) {
			LoggerUtil.add(new WrongInputCastException(String.format(SBundle.getString("error.hkx.write.cast")), e));
		}
	}
}
