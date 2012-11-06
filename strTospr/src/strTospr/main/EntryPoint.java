/**
 * 
 */
package strTospr.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import strTospr.data.ConstantData;
import strTospr.data.ConstantMethods;
import strTospr.data.Utils;

/**
 * @author Prasham
 * 
 */
public class EntryPoint {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out
				.println("Hello, welcome to strTospr, pass us your project name and path to your resources folder, we will create a spreadsheet for your project");
		EntryPoint entryPoint = new EntryPoint();
		entryPoint.startOperations();

	}

	private WritableWorkbook excellFile;
	private WritableSheet excellSheet;

	private HashMap<String, ArrayList<String>> keysValueMap;

	HashSet<String> keysList;
	private File file;

	/**
	 * Start the file reading operations, creates necessary excel file, and
	 * prepare to read android project
	 */
	private void startOperations() {
		BufferedReader br;
		keysList = new HashSet<String>();
		keysValueMap = new HashMap<String, ArrayList<String>>();
		askProjectName();

		askProjectPath();

		if (!ConstantMethods.isEmptyString(ConstantData.PROJECT_PATH)) {
			File projectPath = new File(ConstantData.PROJECT_PATH);
			System.out.println(projectPath + " is a folder?"
					+ (projectPath.isDirectory() ? " yes" : " no"));
			file = new File("C:/StrToSprTest/" + ConstantData.PROJECT_NAME
					+ ".xls");
			if (!file.exists()) {
				new File("C:/StrToSprTest/").mkdir();
			}
			try {
				WorkbookSettings settings = new WorkbookSettings();
				settings.setLocale(new Locale("en", "EN"));
				settings.setUseTemporaryFileDuringWrite(true);

				excellFile = Workbook.createWorkbook(file);
				excellSheet = excellFile.createSheet("Translations", 0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (projectPath.getName().equalsIgnoreCase("res")) {
				System.out
						.println("Congrats! You have just discovered a valuable treasure captain sparrow!!");

				FilenameFilter valuesFilter = new FilenameFilter() {

					@Override
					public boolean accept(File file, String name) {
						// TODO Auto-generated method stub
						if (name.contains("values")) {
							if (file.isDirectory()) {
								File folder = new File(file, name);
								System.out.println("new file " + name
										+ "is folder " + folder.isDirectory());
								if (folder.isDirectory()) {
									File stringsDotXml = new File(folder,
											"strings.xml");
									System.out.println("new file " +stringsDotXml.getParent());
									if (stringsDotXml.exists())
										return true;
									else
										return false;
								} else
									return false;
							} else {
								return false;
							}
						} else

							return false;
					}
				};
				getAvailableLanguages(projectPath, valuesFilter);

			}
		}

	}

	private void getStringFiles(File[] valuesFolder) {
		for (File valuesVersion : valuesFolder) {
			System.out.println("Parsing xml from " + valuesVersion.getName()
					+ "\n");

			String columnName = valuesVersion.getName().substring(
					"values".length());
			if (ConstantMethods.isEmptyString(columnName)) {
				columnName = "default";

			} else {
				columnName = columnName.substring(1);
			}

			int column = findIndexOfLanguage(columnName);
			if (valuesVersion.isDirectory()) {
				File stringsDotXml = new File(valuesVersion, "strings.xml");
				Document document = Utils.getXmlFromFile(stringsDotXml);
				NodeList nodes = document.getElementsByTagName("string");
				for (int i = 0; i < nodes.getLength(); i++) {
					Node item = nodes.item(i);
					String key = "" + item.getAttributes().getNamedItem("name");
					int row = getIndexFromKey(key);

					ArrayList<String> values = keysValueMap.get(key);

					String value = item.getTextContent();

					if (value == null)
						value = "";
					values.add(value);
				}

			}
		}
	}

	// I don't think we may need this method now. so no documentation of it.
	private int getIndexFromKey(String key) {
		String[] keys = new String[keysList.size()];
		keys = keysList.toArray(keys);
		for (int i = 0; i < keys.length; i++) {
			if (keys[i].equalsIgnoreCase(key))
				return i + 1;
		}
		return -1;
	}

	// I don't think we may need this method now. so no documentation of it.
	private int findIndexOfLanguage(String columnName) {
		for (int i = 0; i < ConstantData.LANGUAGES.length; i++) {
			if (ConstantData.LANGUAGES[i].equalsIgnoreCase(columnName))
				return i + 1;
		}
		return -1;
	}

	/**
	 * Get list of available languages provided from project path.
	 * 
	 * @param projectPath
	 *            : Path of your project, must contain res folder
	 * @param valuesFilter
	 *            : {@link FilenameFilter} which filters only values folder
	 */
	private void getAvailableLanguages(File projectPath,
			FilenameFilter valuesFilter) {
		File[] folderList = projectPath.listFiles(valuesFilter);
		String[] availableLanguages = new String[folderList.length];
		String fileName, langName;
		for (int i = 0; i < folderList.length; i++) {
			fileName = folderList[i].getName();
			langName = fileName.substring("values".length());
			if (ConstantMethods.isEmptyString(langName)) {
				langName = "default";

			} else {
				langName = langName.substring(1);
			}

			// availableLanguages[i] = langName;
			FilenameFilter stringDotXmlFilter = new FilenameFilter() {

				@Override
				public boolean accept(File file, String name) {
					// TODO Auto-generated method stub
					return name.contains("strings");
				}
			};

			File[] fileNames = folderList[i].listFiles(stringDotXmlFilter);

			if (fileNames.length > 0) {
				availableLanguages[i] = langName;
			}

			// System.out.println(langName);
		}
		ConstantData.LANGUAGES = availableLanguages;
		getColumns(folderList);
		getStringFiles(folderList);
		writeIntoFile();
	}

	private void writeIntoFile() {
		Iterator keysIterator = keysValueMap.keySet().iterator();

		int i = 1;
		int j = 0;
		do {

			String key = (String) keysIterator.next();
			try {
				String keyToEnter = key.substring("name=\"".length())
						.replaceAll("\"", "");
				addCell(excellSheet, 0, i, keyToEnter);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			ArrayList<String> values = keysValueMap.get(key);

			System.out.println(key);
			System.out.println("{");
			j = 1;
			for (String string : values) {
				System.out.println("\t\t");
				System.out.println(string);
				try {
					addCell(excellSheet, j, i, string);
				} catch (RowsExceededException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				j++;
			}
			System.out.println("};");
			i++;
		} while (keysIterator.hasNext());

		try {
			excellFile.write();
			excellFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * Adds a cell into excel sheet
	 * 
	 * @param sheet
	 *            : Excell Sheet,where you have to write a string
	 * @param column
	 *            : Number of column where the cell is written.
	 * @param row
	 *            : Number of row where the cell is written
	 * @param s
	 *            : Data to be written in cell
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private void addCell(WritableSheet sheet, int column, int row, String s)
			throws WriteException, RowsExceededException {
		Label label;
		// Lets create a times font
		WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
		// Define the cell format

		WritableCellFormat times = new WritableCellFormat(times10pt);
		// Lets automatically wrap the cells
		times.setWrap(true);
		label = new Label(column, row, s, times);
		sheet.addCell(label);
	}

	/**
	 * Fetches keys from the string.xml files of various languages
	 * 
	 * @param folderList
	 *            : List of values(-) folder.
	 */
	private void getColumns(File[] folderList) {

		try {
			addHeaders();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		for (File valuesVersion : folderList) {
			System.out.println("Parsing xml from " + valuesVersion.getName()
					+ "\n");
			if (valuesVersion.isDirectory()) {

				File stringsDotXml = new File(valuesVersion, "strings.xml");
				Document document = Utils.getXmlFromFile(stringsDotXml);
				NodeList nodes = document.getElementsByTagName("string");

				for (int i = 0; i < nodes.getLength(); i++) {
					Node item = nodes.item(i);
					String key = "" + item.getAttributes().getNamedItem("name");
					// System.out.println(key);
					// addCell(excellSheet, 0, i + 1, key);
					addColumn(key);

				}
				System.out.println(keysList.size());
				// excellFile.write();

			}
		}

	}

	private void addHeaders() throws WriteException {
		addHeader(excellSheet, 0, 0, "Keys");
		if (ConstantData.LANGUAGES != null) {
			for (int i = 0; i < ConstantData.LANGUAGES.length; i++) {
				addHeader(excellSheet, i + 1, 0, "" + ConstantData.LANGUAGES[i]);
			}
		}

	}

	private void addHeader(WritableSheet sheet, int column, int row,
			String string) throws WriteException {
		Label label;
		// Lets create a times font
		WritableFont times14pt = new WritableFont(WritableFont.TIMES, 12);
		// Define the cell format

		WritableCellFormat times = new WritableCellFormat(times14pt);
		// Lets automatically wrap the cells
		times.setWrap(true);
		label = new Label(column, row, string, times);
		sheet.addCell(label);

	}

	/**
	 * Asks user to enter project path
	 */
	private void askProjectPath() {
		BufferedReader br;
		System.out
				.println("Time to enter project path (if it contains resources folder it's enough): ");
		br = new BufferedReader(new InputStreamReader(System.in));
		try {
			String projectPath = br.readLine();
			ConstantData.PROJECT_PATH = projectPath;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * Asks user to enter project name
	 * 
	 * Future Feature: We shouldn't ask user to enter project name, instead ask
	 * the project path: i.e. join this and askProjectPath methods so just by
	 * entering project path we can fetch both things at once
	 */
	private void askProjectName() {
		System.out.println("Time to enter project name: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try {
			String projectName = br.readLine();
			ConstantData.PROJECT_NAME = projectName;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * Adds a column in {@link HashMap}
	 * 
	 * @param columnName
	 *            : Column name to check
	 * @return <code>false</code> if the column is already exists, in the
	 *         collection (we don't need to add it now) <code>true</code> if
	 *         this column is sucessfully added in {@link HashMap}
	 */
	private boolean addColumn(String columnName) {

		if (keysValueMap.containsKey(columnName)) {
			// We already have a column so no need to add it again
			return false;
		} else {
			// try to add into colloection
			ArrayList<String> values = new ArrayList<String>();

			keysValueMap.put(columnName, values);
			return true;
		}

	}

}
