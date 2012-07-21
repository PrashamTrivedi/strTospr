/**
 * 
 */
package strTospr.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.management.modelmbean.XMLParseException;
import javax.sql.rowset.spi.XmlReader;

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

	private void startOperations() {
		BufferedReader br;
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

	private void getStringFiles(File projectPath, File[] valuesFolder) {
		for (File valuesVersion : valuesFolder) {
			if (valuesVersion.isDirectory()) {
				File stringsDotXml = new File(valuesVersion, "strings.xml");
//				XmlReader reader = 
			}
		}
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
			System.out.println(langName);
		}
		ConstantData.LANGUAGES = availableLanguages;
		getStringFiles(projectPath, folderList);
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
