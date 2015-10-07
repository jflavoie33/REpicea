package repicea.io.tools;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import repicea.io.FileUtility;
import repicea.io.ImportTests;
import repicea.serial.xml.XmlDeserializer;
import repicea.serial.xml.XmlSerializerChangeMonitor;
import repicea.util.ObjectUtility;
import repicea.util.REpiceaSystem;

public class ImportToolTests {

	static {
		XmlSerializerChangeMonitor.registerClassNameChange("repicea.test.tools.TestRecordReader$FieldID", "repicea.io.tools.TestRecordReader$FieldID");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testStrataFileReading() throws Exception {
		String sourcePath = ObjectUtility.getPackagePath(ImportTests.class) + "TEST6152.accdb";
		
		String targetPath = REpiceaSystem.getJavaIOTmpDir() + "TEST6152.accdb";
		String testIfe = ObjectUtility.getPackagePath(TestRecordReader.class) + "test.ife";
		String results = ObjectUtility.getPackagePath(TestRecordReader.class) + "result1.xml";
				
		if (!FileUtility.copy(sourcePath, targetPath)) {
			throw new IOException("Unable to copy the database file to tmp directory!");
		}

		TestRecordReader recordReader = new TestRecordReader();
		ImportFieldManager ifm = ImportFieldManager.createImportFieldManager(testIfe, targetPath, "TEST6152");
		recordReader.initInScriptMode(ifm);
		List<String> strataList = recordReader.getGroupList();
//		XmlSerializer serializer = new XmlSerializer(results);
//		serializer.writeObject(strataList);
		XmlDeserializer deserializer = new XmlDeserializer(results);
		List<String>  refStrataList = (List<String>) deserializer.readObject();
		assertEquals("Testing for number of elements", refStrataList.size(), strataList.size());
		for (int i = 0; i < strataList.size(); i++) {
			assertEquals("Testing for group name at index " + ((Integer) i).toString(), 
					refStrataList.get(i),
					strataList.get(i));
		}

	}
}