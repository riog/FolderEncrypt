package persistence;

import java.util.Map;

public class FileStoreDBTest {
	public static void main(String[] args) {
		Map<Long, String> fileStore = FileStoreDB.getInstance().getFileStore();
		for(Long key: fileStore.keySet()) {
			System.out.println(key + ": " + fileStore.get(key));
		}
	}
}
