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
import java.util.HashSet;
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

	HashSet<String> keysList;

	private void startOperations() {
		BufferedReader br;
		keysList = new HashSet<String>();
		askProjectName();

		askProjectPath();

		if (!ConstantMethods.isEmptyString(ConstantData.PROJECT_PATH)) {
			File projectPath = new File(ConstantData.PROJECT_PATH);
			System.out.println(projectPath + " is a folder?"
					+ (projectPath.isDirectory() ? " yes" : " no"));

			if (projectPath.getName().equalsIgnoreCase("res")) {
				System.out
						.println("Congrats! You have just discovered a valuable treasure captain sparrow!!");

				FilenameFilter valuesFilter = new FilenameFilter() {

					@Override
					public boolean accept(File file, String name) {
						// TODO Auto-generated method stub
						return name.contains("values");
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
					String value = item.getTextContent();
					try {
						if (row != -1 && column != -1)
							addCell(excellSheet, column, row, value);
					} catch (RowsExceededException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (WriteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO: handle exception
					}

				}

			}
		}
	}

	private int getIndexFromKey(String key) {
		String[] keys = new String[keysList.size()];
		keys = keysList.toArray(keys);
		for (int i = 0; i < keys.length; i++) {
			if (keys[i].equalsIgnoreCase(key))
				return i + 1;
		}
		return -1;
	}

	private int findIndexOfLanguage(String columnName) {
		for (int i = 0; i < ConstantData.LANGUAGES.length; i++) {
			if (ConstantData.LANGUAGES[i].equalsIgnoreCase(columnName))
				return i + 1;
		}
		return -1;
	}

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
			availableLanguages[i] = langName;
			// System.out.println(langName);
		}
		ConstantData.LANGUAGES = availableLanguages;
		getColumns(folderList);
		getStringFiles(folderList);
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

	private void getColumns(File[] folderList) {
		for (File valuesVersion : folderList) {
			System.out.println("Parsing xml from " + valuesVersion.getName()
					+ "\n");
			if (valuesVersion.isDirectory()) {
				File stringsDotXml = new File(valuesVersion, "strings.xml");
				Document document = Utils.getXmlFromFile(stringsDotXml);
				NodeList nodes = document.getElementsByTagName("string");
				try {
					WorkbookSettings settings = new WorkbookSettings();
					settings.setLocale(new Locale("en", "EN"));
					settings.setUseTemporaryFileDuringWrite(true);
					File file = new File("C:/StrToSprTest/"
							+ ConstantData.PROJECT_NAME + ".xls");
					if (!file.exists()) {
						new File("C:/StrToSprTest/").mkdir();
					}
					excellFile = Workbook.createWorkbook(file);
					excellSheet = excellFile.createSheet("Translations", 0);
					addHeaders();

					for (int i = 0; i < nodes.getLength(); i++) {
						Node item = nodes.item(i);
						String key = ""
								+ item.getAttributes().getNamedItem("name");
						// System.out.println(key);
						addCell(excellSheet, 0, i + 1, key);
						keysList.add(key);

					}
					System.out.println(keysList.size());
					// excellFile.write();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				} catch (RowsExceededException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private void addHeaders() throws WriteException {
		addHeader(excellSheet, 0, 0, "Keys");
		if (ConstantData.LANGUAGES != null) {
			for (int i = 0; i < ConstantData.LANGUAGES.length; i++) {
				addCell(excellSheet, i + 1, 0, "" + ConstantData.LANGUAGES[i]);
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

}
