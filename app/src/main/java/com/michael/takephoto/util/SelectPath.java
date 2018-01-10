package com.michael.takephoto.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.michael.takephoto.util.ImageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 意见反馈一些操作类
 */

public class SelectPath {
	//存储选中和拍照的路径
	public static List<String> select_list = new ArrayList<String>();
	//原图地址
	public static List<String> select_path_list = new ArrayList<String>();

	//uuid
	public static List<String> file_list = new ArrayList<String>();

	//反馈意见内容
	public static String content;

	//反馈意见类型
	public static String type = "现有功能改进";

	public static void init() {
		select_list = new ArrayList<String>();
		select_path_list = new ArrayList<String>();
		file_list = new ArrayList<String>();
		content = new String();
	}

	/**
	 * 获取缩略图
	 *
	 * @param path 图片路径
	 */
	public static Bitmap revitionImageSize(String path) throws IOException {
		int degree = ImageManager.readPictureDegree(path);      //读取图片旋转

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);

		Matrix matrix = new Matrix();
		matrix.setRotate(degree);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, options.outWidth, options.outHeight, matrix, true);

		return bitmap;
	}

	/**
	 * 获取缩略图
	 *
	 * @param path 图片路径
	 */
	public static Bitmap revitionImageSize(String path,int inSampleSize) throws IOException {
		int degree = ImageManager.readPictureDegree(path);      //读取图片旋转

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = inSampleSize;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);

		Matrix matrix = new Matrix();
		matrix.setRotate(degree);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, options.outWidth, options.outHeight, matrix, true);

		return bitmap;
	}
}
