package py.com.excelsis.sicca.util;

import org.richfaces.model.UploadItem;

public class RespuestaFileUploader {
    byte[] byteArray;
    UploadItem item;

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public UploadItem getItem() {
        return item;
    }

    public void setItem(UploadItem item) {
        this.item = item;
    }
}
