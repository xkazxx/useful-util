package com.xkazxx.fils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.*;


/**
 * 复制jar包中的文件或者文件夹到当前文件系统中
 *
 * @version v0.1
 * @author: created by xkazxx
 * @description: description
 * @date: 2023/4/26 23:12
 **/
public class CopyJarFileToSystemUtil {
	private final static Logger log = LoggerFactory.getLogger(CopyJarFileToSystemUtil.class);

	/**
	 * 复制单个文件 从classpath中读取文件复制
	 *
	 * @param path    不能以/开头   指向文件不能是目录
	 * @param newpath 指向文件不能是目录
	 */
	public static void copyFileFromJar(String path, String newpath) {
		try {
			//创建新文件
			makeFile(newpath);
			//获取文件流
			InputStream in = new String().getClass().getClassLoader().getResourceAsStream(path);
			//将流写入新文件
			write2File(in, newpath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 复制path目录下所有文件
	 *
	 * @param path    文件目录 不能以/开头
	 * @param newpath 新文件目录
	 */
	public static void dirCopyFileFromJar(String path, String newpath) {
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		try {
			//获取所有匹配的文件
			Resource[] resources = resolver.getResources(path + "/*");
			//打印有多少文件
			log.info("*****************" + resources.length);
			for (int i = 0; i < resources.length; i++) {
				Resource resource = resources[i];
				try {
					//以jar运行时，resource.getFile().isFile() 无法获取文件类型，会报异常，抓取异常后直接生成新的文件即可；以非jar运行时，需要判断文件类型，避免如果是目录会复制错误，将目录写成文件。
					if (resource.getFile().isFile()) {
						makeFile(newpath + "/" + resource.getFilename());
						InputStream stream = resource.getInputStream();
						write2File(stream, newpath + "/" + resource.getFilename());
					}
				} catch (Exception e) {
					log.info("-------------" + e.getMessage());
					makeFile(newpath + "/" + resource.getFilename());
					InputStream stream = resource.getInputStream();
					write2File(stream, newpath + "/" + resource.getFilename());
				}
			}
		} catch (Exception e) {
			log.info("-------------" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 创建文件
	 *
	 * @param path 全路径 指向文件
	 * @return
	 */
	public static boolean makeFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			log.info("文件已存在！");
			return false;
		}
		if (path.endsWith(File.separator)) {
			log.info("不能为目录！");
			return false;
		}
		if (!file.getParentFile().exists()) {
			if (!file.getParentFile().mkdirs()) {
				log.info("创建目标文件所在目录失败！");
				return false;
			}
		}
		try {
			if (file.createNewFile()) {
				log.info("创建文件" + path + "成功！");
				return true;
			} else {
				log.info("创建文件" + path + "失败！");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.info("创建文件" + path + "失败！" + e.getMessage());
			return false;
		}
	}

	/**
	 * 输入流写入文件
	 *
	 * @param is       输入流
	 * @param filePath 文件保存目录路径
	 * @throws IOException
	 */
	public static void write2File(InputStream is, String filePath) throws IOException {
		OutputStream os = new FileOutputStream(filePath);
		int len = 8192;
		byte[] buffer = new byte[len];
		while ((len = is.read(buffer, 0, len)) != -1) {
			os.write(buffer, 0, len);
		}
		os.close();
		is.close();
	}
}


