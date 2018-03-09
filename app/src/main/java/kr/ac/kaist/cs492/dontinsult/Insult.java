package kr.ac.kaist.cs492.dontinsult;

/**
 * Created by yearnning on 15. 7. 4..
 */
public class Insult {

    private String file_name = null;
    private boolean is_header = false;
    private String name = null;

    /**
     * @param name
     * @param fileName
     * @param is_header
     * @return
     */
    public static Insult newInstance(String name, String fileName, boolean is_header) {
        Insult insult = new Insult();
        insult.setName(name);
        insult.setIs_header(is_header);
        insult.setFile_name(fileName);
        return insult;
    }

    /**
     * @return
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public boolean is_header() {
        return is_header;
    }

    public void setIs_header(boolean is_header) {
        this.is_header = is_header;
    }


}
