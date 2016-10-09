package util;

import java.util.ArrayList;

/**
 * Created by vikrant on 8/10/16.
 */

public class Header {
    ArrayList<String> fields = new ArrayList<String>();

    public Header(ArrayList<String> fields) {
        this.fields = fields;
    }

    public Header() {
    }

    public Header addHeader(String field) {
        fields.add(field);
        return this;
    }

    public String getHeader() {
        String header = "";
        for (int i = 0; i < fields.size(); i++) {
            if (i == 0) header = fields.get(i);
            else {
                header = header + K.FIELD_SEPARATOR + fields.get(i);
            }
        }
        return header;
    }
    public void removeHeader(String field) {
        fields.remove(field);
    }

    @Override
    public String toString() {
        return getHeader();
    }
}
