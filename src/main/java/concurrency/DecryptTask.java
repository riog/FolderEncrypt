package concurrency;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import javax.swing.SwingWorker;

import components.FolderEncrypt;

import encryption.DesEncrypter;

public class DecryptTask extends
		SwingWorker<Map<String, Object>, Map<String, Object>> {

	private FolderEncrypt fe;
	private DesEncrypter des;

	public DecryptTask(FolderEncrypt fe) {
		this.fe = fe;
		this.des = new DesEncrypter();
	}

	@Override
	protected Map<String, Object> doInBackground() throws Exception {
		fe.enableButtons(false);
		decrypt(fe.getSrcDir(), fe.getDestDir());
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("done", true);
		return hashMap;
	}

	public void decrypt(File sourceFile, File dest) {
		File dir = sourceFile;

		File[] children = dir.listFiles();
		if (children == null) {
			// Either dir does not exist or is not a directory
		} else {
			for (int i = 0; i < children.length; i++) {
				// Get filename of file or directory
				File src = children[i];
				SortedMap<Long, String> fileStore = fe.getFileStore();
				String t = src.getName();
				String filename = fileStore.get(t);
				
				File destFile = new File(filename);
				try {
					des.decrypt(new FileInputStream(src), new FileOutputStream(
							destFile), fe.getPassword());
					HashMap<String, Object> current = new HashMap<String, Object>();
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
		for (Map m : chunks) {
			fe.sendMessage("Decrypted: " + m.get("current"));
		}
	}

	@Override
	protected void done() {
		fe.sendMessage("Decryption finished.");
		fe.enableButtons(true);
		fe.clearTextFields();
	}
}
