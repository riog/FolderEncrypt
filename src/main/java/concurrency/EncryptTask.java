package concurrency;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingWorker;

import persistence.FileStoreDB;

import components.FolderEncrypt;

import encryption.DesEncrypter;

public class EncryptTask extends SwingWorker<Map<String, Object>, Map<String, Object>> {

private FolderEncrypt app;
private DesEncrypter des;
public EncryptTask(FolderEncrypt fe) {
	this.app = fe;
	this.des = new DesEncrypter();
}
@Override
protected Map<String, Object> doInBackground() throws Exception {
	app.enableButtons(false);
	encrypt(app.getSrcDir(), app.getDestDir());
	Map<String,Object> hashMap = new HashMap<String, Object>();
	hashMap.put("done", true);
	return hashMap;
}

	public void encrypt(File sourceFile, File dest) {
		File dir = sourceFile;

		File[] children = dir.listFiles();
		if (children == null) {
		    // Either dir does not exist or is not a directory
		} else {
			String password = app.getPassword();
		    for (int i=0; i<children.length; i++) {
		        // Get filename of file or directory
		        File src = children[i];
		        Long t = System.currentTimeMillis();
		        String name = src.getName();
				String filename = dest.getAbsolutePath() + File.separator + name;
				String maskdName = dest.getAbsolutePath() + File.separator + t;
				FileStoreDB.getInstance().storeEncryptedFile( t, filename);
				
				File destFile = new File(maskdName);
				try {
					des.encrypt(new FileInputStream(src), new FileOutputStream(destFile), password);
					HashMap<String,Object> current = new HashMap<String,Object>();
					current.put("current", src.getAbsoluteFile());
					publish(current);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
		    }
		}
	}
	
@Override
	protected void process(List<Map<String, Object>> chunks) {
		for(Map m: chunks) {
			app.sendMessage("Encrypted: " + m.get("current"));	
		}
	}
@Override
	protected void done() {
		app.enableButtons(true);
		app.clearTextFields();
		app.sendMessage("Encryption finished.");
	}
}
