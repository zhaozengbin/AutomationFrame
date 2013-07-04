/**
 * 项目名：AutomationFrame <br>
 * 包名：com.automation.utils <br>
 * 文件名：JMagickUtils.java <br>
 * 版本信息：TODO <br>
 * 作者：赵增斌 E-mail：zhaozengbin@gmail.com QQ:4415599 weibo:http://weibo.com/zhaozengbin<br>
 * 日期：2013-6-24-上午9:47:38<br>
 * Copyright (c) 2013 赵增斌-版权所有<br>
 *
 */
package com.automation.utils;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import magick.CompositeOperator;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * 类名称：JMagickUtils <br>
 * 类描述：图片工具类 <br>
 * 创建人：赵增斌 <br>
 * 修改人：赵增斌 <br>
 * 修改时间：2013-6-24 上午9:47:38 <br>
 * 修改备注：TODO <br>
 * 
 */
public class JMagickUtils {

	private String toPath;

	private InputStream input;

	// private final static String YIYA_LOGO_BG ="d:\\t2\\watermark_bg.png";
	// private final static String YIYA_LOGO_LOGO =
	// "d:\\t2\\watermark_logo.png";
	private final static String YIYA_LOGO_BG = PathUtils.getRootPath()
			+ "/static/common/images/watermark_bg.png"; // 水印背景图片 白色半透明
	private final static String YIYA_LOGO_LOGO = PathUtils.getRootPath()
			+ "/static/common/images/watermark_logo.png";// 水印图片 名称+网址

	/**
	 * 标题：构造器 <br>
	 * 描述：TODO <br>
	 * 作者：赵增斌 E-mail：zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 上午9:48:15 <br>
	 */
	public JMagickUtils() {
		// 不能漏掉这个，不然jmagick.jar的路径找不到
		System.setProperty("jmagick.systemclassloader", "no");
	}

	public static JMagickUtils Utils = new JMagickUtils();

	/**
	 * 方法：ofUrl <br>
	 * 描述： 获取网络图片 <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 上午9:48:21 <br>
	 * 
	 * @param picUrl
	 * @return
	 * @throws IOException
	 */
	public JMagickUtils ofUrl(String picUrl) throws IOException {
		URL url = new URL(picUrl);
		URLConnection urlConnection = url.openConnection();// 打开url连接
		InputStream is = urlConnection.getInputStream();
		urlConnection.getContentType();
		return of(is);
	}

	/**
	 * 方法：ofUrl <br>
	 * 描述：获取盗链的图片 <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 上午9:48:28 <br>
	 * 
	 * @param url
	 * @param refererURL
	 * @return
	 * @throws IOException
	 */
	public JMagickUtils ofUrl(String url, String refererURL) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url)
				.openConnection();
		conn.setRequestProperty("User-Agent",
				"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Maxthon;)");
		conn.setRequestProperty("Accept-Encoding", "gzip");
		conn.setRequestProperty("referer", refererURL);
		conn.setRequestProperty("cookie", "data");
		InputStream is = conn.getInputStream();
		return of(is);
	}

	/**
	 * 方法：of <br>
	 * 描述：TODO <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 上午9:48:47 <br>
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public JMagickUtils of(String path) throws IOException {
		File file = new File(path);
		return of(file);
	}

	/**
	 * 方法：of <br>
	 * 描述：TODO <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 上午9:48:52 <br>
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public JMagickUtils of(File file) throws IOException {
		InputStream input = new FileInputStream(file);
		return of(input);
	}

	/**
	 * 方法：of <br>
	 * 描述：TODO <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 上午9:48:58 <br>
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public JMagickUtils of(InputStream input) throws IOException {
		this.input = input;
		return this;
	}

	/**
	 * 方法：to <br>
	 * 描述：TODO <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 上午9:49:03 <br>
	 * 
	 * @param path
	 * @return
	 */
	public JMagickUtils to(String path) {
		this.toPath = path;
		return this;
	}

	/**
	 * 方法：create <br>
	 * 描述： 创建图片，默认增加图片logo水印 <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 上午9:49:08 <br>
	 * 
	 * @throws Exception
	 */
	public void create() throws Exception {
		create(true);
	}

	/**
	 * 方法：create <br>
	 * 描述：创建图片 <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 上午9:49:16 <br>
	 * 
	 * @param isAddLogo
	 *            是否增加水印
	 * @throws Exception
	 */
	public void create(boolean isAddLogo) throws Exception {
		createDir();// 创建文件夹
		ImageInfo info = null;
		MagickImage image = null;
		MagickImage mask = null;// 水印
		int width, height;
		try {
			info = new ImageInfo();
			image = new MagickImage(info, getBytes());
			image.setFileName(toPath);
			width = (int) image.getDimension().getWidth();
			height = (int) image.getDimension().getHeight();
			// 添加水印 start
			File fileBg = new File(YIYA_LOGO_BG);
			File fileLogo = new File(YIYA_LOGO_LOGO);
			if (fileBg.exists() && fileLogo.exists() && isAddLogo) {
				mask = new MagickImage(new ImageInfo(YIYA_LOGO_BG));
				image.compositeImage(CompositeOperator.AtopCompositeOp, mask,
						0, height - 30);
				mask = new MagickImage(new ImageInfo(YIYA_LOGO_LOGO));
				image.compositeImage(CompositeOperator.AtopCompositeOp, mask,
						0, height - 30);
			}
			// 添加水印 end
			image.writeImage(info);
			System.out.println("创建图片：" + toPath);
		} finally {
			if (image != null) {
				image.destroyImages();
			}
			info = null;
			image = null;
			mask = null;
			destroy();
		}
	}

	/**
	 * 方法：create <br>
	 * 描述：创建图片，指定图片宽度，高度自动按比例缩放 <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 上午9:49:28 <br>
	 * 
	 * @param width
	 *            宽度
	 * @throws Exception
	 */
	public void create(int width) throws Exception {
		createDir();// 创建文件夹
		ImageInfo info = null;
		MagickImage image = null;
		Dimension imageDim = null;
		MagickImage scaled = null;
		try {
			info = new ImageInfo();
			image = new MagickImage(info, getBytes());
			imageDim = image.getDimension();
			int imageWidth = (int) imageDim.getWidth();
			int imageHeight = (int) imageDim.getHeight();
			int target_width = width;
			int w = target_width;
			int h = (int) Math
					.round((imageHeight * target_width * 1.0 / imageWidth));
			scaled = image.scaleImage(w, h);// minImage.cropImage(rect);
			scaled.setFileName(toPath);
			scaled.writeImage(info);
			System.out.println("创建图片：" + toPath);
		} finally {
			if (scaled != null) {
				scaled.destroyImages();
			}
			info = null;
			image = null;
			imageDim = null;
			scaled = null;
			destroy();
		}
	}

	/**
	 * 方法：create <br>
	 * 描述：TODO <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 上午9:52:02 <br>
	 * 
	 * @param width
	 *            指定图片宽度
	 * @param height
	 *            指定图片高度
	 * @throws Exception
	 */
	public void create(int width, int height) throws Exception {
		createDir();// 创建文件夹
		ImageInfo info = null;
		MagickImage image = null;
		Dimension imageDim = null;
		MagickImage scaled = null;
		MagickImage minImage = null;
		Rectangle rect = null;
		int x = 0, y = 0, w = 0, h = 0, imageWidth = 0, imageHeight = 0;
		try {
			info = new ImageInfo();
			image = new MagickImage(info, getBytes());
			imageDim = image.getDimension();
			imageWidth = (int) imageDim.getWidth();
			imageHeight = (int) imageDim.getHeight();

			// 得到合适的压缩大小，按比例。
			if (imageWidth <= imageHeight) {
				w = width;
				h = (int) Math.round((imageHeight * width * 1.0 / imageWidth));
				if (width > h) {
					y = (h - height) / 2;
				}
			} else {
				h = height;
				w = (int) Math.round((imageWidth * height * 1.0 / imageHeight));
				x = (w - width) / 2;
			}
			minImage = image.scaleImage(w, h);
			rect = new Rectangle(x, y, width, height);
			scaled = minImage.cropImage(rect);
			scaled.setFileName(toPath);
			scaled.writeImage(info);
			System.out.println("创建图片：" + toPath);
		} finally {
			if (scaled != null) {
				image.destroyImages();
				scaled.destroyImages();
				destroy();
			}
			info = null;
			image = null;
			imageDim = null;
			scaled = null;
			minImage = null;
		}
	}

	/**
	 * 方法：main <br>
	 * 描述：TODO <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 上午9:52:16 <br>
	 * 
	 * @param args
	 * @throws MagickException
	 */
	public static void main(String[] args) throws MagickException {
		String filePath = "D:\\t\\3.jpg";
		String toPath = "D:\\t2\\001_magick.jpg";
		try {
			String url = "http://www.eale.cc/UploadFiles/20127910293960.jpg";
			url = "http://www.9441.com/uploads/allimg/c120703/13412UYTZ10-13YK.jpg";
			// 获取网络图片
			JMagickUtils.Utils.ofUrl(url).to("D:\\t2\\1.jpg").create();
			// 获取盗链图片
			JMagickUtils.Utils.ofUrl(url, "http://www.9441.com/")
					.to("D:\\t2\\1.jpg").create();

			JMagickUtils.Utils.of("d:\\src.jpg").to("D:\\t2\\1.jpg").create();
			// 切缩略图片
			JMagickUtils.Utils.of("d:\\src.jpg").to("D:\\t2\\1.jpg")
					.create(200, 200);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 方法：getBytes <br>
	 * 描述：TODO <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 上午9:52:20 <br>
	 * 
	 * @return
	 * @throws IOException
	 */
	public byte[] getBytes() throws IOException {
		byte[] bytes = IOUtils.toByteArray(input);
		return bytes;
	}

	/**
	 * 方法：getInput <br>
	 * 描述：TODO <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 上午9:52:26 <br>
	 * 
	 * @return
	 */
	public InputStream getInput() {
		return input;
	}

	/**
	 * 方法：getBuffer <br>
	 * 描述：TODO <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 上午9:52:30 <br>
	 * 
	 * @return
	 * @throws IOException
	 */
	public BufferedImage getBuffer() throws IOException {
		BufferedImage buffer = ImageIO.read(input);
		return buffer;
	}

	/**
	 * 方法：createDir <br>
	 * 描述：创建文件夹 <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 上午9:52:35 <br>
	 * 
	 * @throws Exception
	 */
	private void createDir() throws Exception {
		if (StringUtils.isBlank(toPath)) {
			throw new Exception("未指定文件路径");
		}
		File file = new File(toPath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
	}

	/**
	 * 方法：destroy <br>
	 * 描述：TODO <br>
	 * 作者：赵增斌 E-mail:zhaozengbin@gmail.com QQ:4415599
	 * weibo:http://weibo.com/zhaozengbin <br>
	 * 日期： 2013-6-24 上午9:52:42 <br>
	 */
	public void destroy() {
		this.input = null;
	}

}
