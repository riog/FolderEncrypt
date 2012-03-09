package persistence;

import java.util.Map;

import net.kotek.jdbm.DB;
import net.kotek.jdbm.DBMaker;

public class FileStoreDB {
	private DB db;
	private Map<Long, String> fileStore;
	private static FileStoreDB instance;
	
	public static FileStoreDB getInstance() {
		if(instance == null) {
			instance = new FileStoreDB();
		}
		return instance;
	}
	
	public Map<Long, String> getFileStore() {
		if(db == null) {
			db = new DBMaker("fedata").build();
		}
		if(fileStore == null) {
			fileStore = db.getTreeMap("fileStore");
			if(fileStore == null) {
				fileStore = db.createTreeMap("fileStore");
			}	
		}
		return fileStore;
	}
	public void storeEncryptedFile( Long timestamp, String encryptedName) {
		Map<Long, String> fileStore = getFileStore();
		fileStore.put(timestamp, encryptedName);
		db.commit();
	}
	public void closeDB() {
		db.close();
	}
}
