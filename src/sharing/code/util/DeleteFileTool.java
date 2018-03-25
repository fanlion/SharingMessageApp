package sharing.code.util;

import java.io.File;

/**
 * 锟斤拷锟斤拷锟斤拷:
 * 锟斤拷锟斤拷删锟斤拷指锟斤拷路锟斤拷锟斤拷锟侥硷拷锟斤拷锟侥硷拷锟叫ｏ拷锟斤拷锟斤拷锟侥硷拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟捷ｏ拷
 *
 */
public class DeleteFileTool {

	/**
	 *  锟斤拷锟斤拷路锟斤拷删锟斤拷指锟斤拷锟斤拷目录锟斤拷锟侥硷拷锟斤拷锟斤拷锟桔达拷锟斤拷锟斤拷锟�
	 *@param sPath  要删锟斤拷锟斤拷目录锟斤拷锟侥硷拷
	 *@return 删锟斤拷锟缴癸拷锟斤拷锟斤拷 true锟斤拷锟斤拷锟津返伙拷 false锟斤拷
	 */
	public static boolean DeleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 锟叫讹拷目录锟斤拷锟侥硷拷锟角凤拷锟斤拷锟�
		if (!file.exists()) {  // 锟斤拷锟斤拷锟节凤拷锟斤拷 false
			return flag;
		} else {
			// 锟叫讹拷锟角凤拷为锟侥硷拷
			if (file.isFile()) {  // 为锟侥硷拷时锟斤拷锟斤拷删锟斤拷锟侥硷拷锟斤拷锟斤拷
				return deleteFile(sPath);
			} else {  // 为目录时锟斤拷锟斤拷删锟斤拷目录锟斤拷锟斤拷
				return deleteDirectory(sPath);
			}
		}
	}

	/**
	 * 删锟斤拷锟斤拷锟斤拷锟侥硷拷
	 * @param -  sPath    锟斤拷删锟斤拷锟侥硷拷锟斤拷锟侥硷拷锟斤拷
	 * @return - 锟斤拷锟斤拷锟侥硷拷删锟斤拷锟缴癸拷锟斤拷锟斤拷true锟斤拷锟斤拷锟津返伙拷false
	 */
	private static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路锟斤拷为锟侥硷拷锟揭诧拷为锟斤拷锟斤拷锟斤拷锟缴撅拷锟�
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 删锟斤拷目录锟斤拷锟侥硷拷锟叫ｏ拷锟皆硷拷目录锟铰碉拷锟侥硷拷
	 * @param   sPath 锟斤拷删锟斤拷目录锟斤拷锟侥硷拷路锟斤拷
	 * @return  目录删锟斤拷锟缴癸拷锟斤拷锟斤拷true锟斤拷锟斤拷锟津返伙拷false
	 */
	private static boolean deleteDirectory(String sPath) {
		//锟斤拷锟絪Path锟斤拷锟斤拷锟侥硷拷锟街革拷锟斤拷锟斤拷尾锟斤拷锟皆讹拷锟斤拷锟斤拷募锟斤拷指锟斤拷锟�
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		//锟斤拷锟絛ir锟斤拷应锟斤拷锟侥硷拷锟斤拷锟斤拷锟节ｏ拷锟斤拷锟竭诧拷锟斤拷一锟斤拷目录锟斤拷锟斤拷锟剿筹拷
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		//删锟斤拷锟侥硷拷锟斤拷锟铰碉拷锟斤拷锟斤拷锟侥硷拷(锟斤拷锟斤拷锟斤拷目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			//删锟斤拷锟斤拷锟侥硷拷
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) break;
			} //删锟斤拷锟斤拷目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) break;
			}
		}
		if (!flag) return false;
		//删锟斤拷锟斤拷前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}
}
