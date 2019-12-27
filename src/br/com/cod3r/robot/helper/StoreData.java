package br.com.cod3r.robot.helper;

import java.io.File;
import java.io.IOException;

public class StoreData {

	private String lastFilePath;
	private int resetKey;
	private int resetModifiers;
	private int prevKey;
	private int prevModifiers;
	private int nextKey;
	private int nextModifiers;

	private static StoreData instance;
	private static String folder = System.getProperty("user.home") + File.separator + ".cod3r";

	private StoreData() {
	}

	public String getLastFilePath() {
		return lastFilePath;
	}

	public File getLastFile() {
		try {
			if (getLastFilePath() != null) {
				return new File(getLastFilePath());
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public void setLastFile(File lastFile) {
		if (lastFile != null) {
			this.lastFilePath = lastFile.getAbsolutePath();
		}
	}

	public String getLastFolderPath() {
		File lastFolder = getLastFolder();
		if (lastFolder != null) {
			return lastFolder.getAbsolutePath();
		}
		return null;
	}

	public File getLastFolder() {
		File lastFile = getLastFile();
		if (lastFile != null) {
			return lastFile.getParentFile();
		}
		return null;
	}

	public void setLastFilePath(String lastFilePath) {
		this.lastFilePath = lastFilePath;
	}

	public int getResetKey() {
		return resetKey;
	}

	public void setResetKey(int resetKey) {
		this.resetKey = resetKey;
	}

	public int getResetModifiers() {
		return resetModifiers;
	}

	public void setResetModifiers(int resetModifiers) {
		this.resetModifiers = resetModifiers;
	}

	public int getPrevKey() {
		return prevKey;
	}

	public void setPrevKey(int prevKey) {
		this.prevKey = prevKey;
	}

	public int getPrevModifiers() {
		return prevModifiers;
	}

	public void setPrevModifiers(int prevModifiers) {
		this.prevModifiers = prevModifiers;
	}

	public int getNextKey() {
		return nextKey;
	}

	public void setNextKey(int nextKey) {
		this.nextKey = nextKey;
	}

	public int getNextModifiers() {
		return nextModifiers;
	}

	public void setNextModifiers(int nextModifiers) {
		this.nextModifiers = nextModifiers;
	}

	public static StoreData getInstance() {
		if (instance == null) {
			File dir = new File(folder);
			if (!dir.isDirectory()) {
				dir.mkdir();
			}

			File storeFile = new File(folder + File.separator + "robot");
			if (storeFile.isFile()) {
				try {
					String content = Files.readFile(storeFile.getAbsolutePath());
					instance = fromString(content);
				} catch (Exception e) {
					instance = new StoreData();
				}
			} else {
				instance = new StoreData();
			}
		}

		return instance;
	}

	public void save() {
		try {
			File storeFile = new File(folder + File.separator + "robot");
			if (storeFile.exists()) {
				storeFile.createNewFile();
			}

			Files.writeFile(storeFile.getAbsolutePath(), this.toString());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (lastFilePath != null) {
			sb.append("lastFilePath:" + lastFilePath + ";");			
		}
		sb.append("resetKey:" + resetKey + ";");
		sb.append("resetModifiers:" + resetModifiers + ";");
		sb.append("prevKey:" + prevKey + ";");
		sb.append("prevModifiers:" + prevModifiers + ";");
		sb.append("nextKey:" + nextKey + ";");
		sb.append("nextModifiers:" + nextModifiers + ";");
		return sb.toString();
	}

	public static StoreData fromString(String content) {
		String[] attrs = content.split(";");
		StoreData data = new StoreData();
		data.setLastFilePath(getValue(attrs, "lastFilePath"));
		data.setResetKey(Integer.parseInt(getValue(attrs, "resetKey")));
		data.setResetModifiers(Integer.parseInt(getValue(attrs, "resetModifiers")));
		data.setPrevKey(Integer.parseInt(getValue(attrs, "prevKey")));
		data.setPrevModifiers(Integer.parseInt(getValue(attrs, "prevModifiers")));
		data.setNextKey(Integer.parseInt(getValue(attrs, "nextKey")));
		data.setNextModifiers(Integer.parseInt(getValue(attrs, "nextModifiers")));
		return data;
	}

	public static String getValue(String[] attrs, String name) {
		for (String attr : attrs) {
			if (attr.startsWith(name)) {
				return attr.split(":")[1];
			}
		}
		return null;
	}

}
