package pl.sebcel.gpstracker.utils;

import java.io.PrintStream;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

public class FileUtils {

	public String findRoot() {
		Enumeration e = FileSystemRegistry.listRoots();
		while (e.hasMoreElements()) {
			String root = (String) e.nextElement();
			if (checkRoot(root)) {
				return root;
			}
		}
		return null;
	}

	private boolean checkRoot(String root) {
		try {
			String uri = "file:///" + root + "root_test.txt";
			FileConnection fconn = (FileConnection) Connector.open(uri);
			if (!fconn.exists()) {
				fconn.create();
			}

			PrintStream out = new PrintStream(fconn.openOutputStream());

			out.println("Test!");
			out.close();

			fconn.delete();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}