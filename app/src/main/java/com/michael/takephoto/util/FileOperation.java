package com.michael.takephoto.util;

import android.os.Environment;
import android.os.StatFs;

import com.michael.takephoto.consts.LogicConsts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Vector;


public class FileOperation {

	private String filename = null;
	private String suffix = ".dat";
	public boolean insertdata = false;
	private String secondarydir = "";
	private File file;

	/**
	 * 二级目录名
	 *
	 * @param dir
	 */
	public FileOperation(String dir, String suffix) {
		secondarydir = dir;
		if (suffix != null)
			this.suffix = suffix;
	}

	/**
	 * 创建打开文件
	 *
	 * @param name
	 */
	public boolean initFile(String name) {
		filename = LogicConsts.PATH + secondarydir + "/";
		if (FileOperation.checkSDcard()) {

			try {
				if (name != null && name.length() > 1) {
					file = new File(filename);
					if (!file.exists()) {
						file.mkdirs();
					}
					if (name != null && !"".equals(name)) {
						filename = filename + name + suffix;
						file = new File(filename);
						if (!file.exists()) {
							file.createNewFile();// 文件不存在则新建
						}
					}
				} else {
					file = new File(filename);
					if (!file.exists()) {
						file.mkdirs();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("创建文件失败");
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 创建打开文件
	 *
	 * @param name
	 */
	public boolean initFile(String path, String name) {
		filename = path + secondarydir + "/";
		if (FileOperation.checkSDcard()) {

			try {
				if (name != null && name.length() > 1) {
					file = new File(filename);
					if (!file.exists()) {
						file.mkdirs();
					}
					if (name != null && !"".equals(name)) {
						filename = filename + name + suffix;
						file = new File(filename);
						if (!file.exists()) {
							file.createNewFile();// 文件不存在则新建
						}
					}
				} else {
					file = new File(filename);
					if (!file.exists()) {
						file.mkdirs();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("创建文件失败");
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	public synchronized static boolean lowSDcard() {
		if (getAvailaleSize() <= 5 * 1024 * 1024) {
			return true;
		}
		return false;
	}

	/*
	 * 判断是否有SD卡
	 */
	public synchronized static boolean checkSDcard() {
		String status = android.os.Environment.getExternalStorageState();
		if (status.equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static long getAvailaleSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		/* 获取block的SIZE */
		long blockSize = stat.getBlockSize();
		/* 空闲的Block的数量 */
		long availableBlocks = stat.getAvailableBlocks();
		/* 返回bit大小值 */
		return availableBlocks * blockSize;
	}

	/**
	 * 获取文件列表
	 *
	 * @return
	 */
	public String[] getFileList() {
		try {
			if (FileOperation.checkSDcard()) {
				Vector<String> fileName = new Vector<String>();
				if (file.exists() && file.isDirectory()) {
					String[] str = file.list();
					for (String s : str) {
						if (new File(filename + s).isFile()) {
							if(-1==s.lastIndexOf(".")){
								fileName.addElement(s);
							}else{
								fileName.addElement(s.substring(0, s.lastIndexOf(".")));
							}
						}
					}
					return fileName.toArray(new String[] {});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}



	/**
	 * 添加一行记录
	 *
	 * @param data
	 */
	public boolean addLine(byte[] data) {
		/*
		 * try{ FileOutputStream fos = new FileOutputStream(file);
		 * ObjectOutputStream objOS = new ObjectOutputStream(fos);
		 * objOS.write(data); objOS.flush(); objOS.close();
		 *
		 * }catch(Exception e){ System.out.println("写存储卡错误");
		 * e.printStackTrace(); return false; }
		 */
		if (FileOperation.checkSDcard()) {
			if (!file.exists()) {
				return false;
			}
			try {

				FileOutputStream fos = new FileOutputStream(file);
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e) {
				System.out.println("写存储卡错误");
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				System.out.println("写存储卡错误");
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public void closeFile() {
		try {
			if (file != null) {
				file = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回当前文件流
	 *
	 * @return
	 */
	public byte[] getData() {
		if (FileOperation.checkSDcard()) {
			if (file == null || !file.exists()) {
				return null;
			}
			int length = (int) file.length();
			byte[] b = new byte[length];
			try {
				FileInputStream fis = new FileInputStream(file);
				fis.read(b, 0, length);
				fis.close();
			} catch (FileNotFoundException e) {

			} catch (IOException e) {
			}
			return length != 0 ? b : null;
		}
		return null;
	}


	/**
	 * 删除文件
	 */
	public boolean deleteFile() {
		try {
			if (file != null)
				file.delete();
		} catch (Exception e) {
			System.out.println("删除文件IO异常");

			return false;
		}
		return true;
	}

	/**
	 * 判断文件是否存在
	 *
	 * @param name
	 * @return
	 */
	public boolean exist(String name) {
		String mypath = LogicConsts.PATH + secondarydir + "/";
		File file = new File(mypath, name + suffix);
		return file.exists();
	}


	/**
	 * 根据需要创建应用的二级文件夹，开头不需要斜杠
	 * @param pathName
	 * @return
	 */
	public static File createDic(String pathName){
		String fullPath=LogicConsts.PATH+pathName;
		File file=new File(fullPath);
		if(file.exists()&&file.isDirectory()){
			//文件夹已经存在
		}else{
			file.mkdirs();
		}
		return file;
	}



	/**
	 * 复制文件
	 * @param sourceFile
	 * @param destFile
	 * @return
	 */
	public static void copyFile(File sourceFile, File destFile) {
		try{
			FileChannel destination=new FileOutputStream(destFile).getChannel();
			FileChannel source=new FileInputStream(sourceFile).getChannel();
			destination.transferFrom(source, 0, source.size());
			if(source!=null){
				source.close();
			}
			if(destination!=null){
				destination.close();
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	/**
	 * 删除目录 如果目录下有子目录或文件，也全部删除
	 *
	 * @param pathName
	 */
	public static boolean deletDirectory(String pathName) {
		boolean del = false;
		if (FileOperation.checkSDcard()) {
			File file = new File(pathName);
			if (file.exists()) {
				if (file.isFile()) {
					file.delete();
				} else if (file.isDirectory()) {
					//发送队列文件和发送的文件不清空
					if(pathName.contains("sendqueue")||pathName.contains("tmpUploadDic")){     //不删除发送队列相关文件夹
						return true;
					}else{
						String[] f = file.list();
						for (int i = 0; i < f.length; i++) {
							deletDirectory(pathName + "/" + f[i]);
						}
						del = file.delete();
					}
				}
			}
		}
		return del;
	}

	public static void deleteAllFile() {
		delDir(LogicConsts.PATH);
	}

	/**
	 * 删除目录 如果目录下有子目录或文件，也全部删除
	 */
	private static void delDir(String pathName) {

		boolean del = false;
		File file = new File(pathName);
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] f = file.list();
				for (int i = 0; i < f.length; i++) {
					deletDirectory(pathName + "/" + f[i]);
				}
				del = file.delete();
			}
		}
	}

	public File getFile() {
		return file;
	}

}
