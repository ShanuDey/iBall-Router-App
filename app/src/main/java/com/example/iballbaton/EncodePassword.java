package com.example.iballbaton;

public class EncodePassword {
    //Base64 encode
    String base64EncodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    int[] base64DecodeChars = new int[]{
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
            -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
            -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
            41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1
    };
    private String utf16to8(String str) {
        String out;
        int c, len,i;

        out = "";
        len = str.length();
        for ( i = 0; i < len; i++) {
            c = charCodeAt(str,i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                out += str.charAt(i);
            } else if (c > 0x07FF) {
                out += Character.toString((char) (0xE0 | ((c >> 12) & 0x0F)));
                out += Character.toString((char) (0x80 | ((c >> 6) & 0x3F)));
                out += Character.toString((char) (0x80 | ((c >> 0) & 0x3F)));
            } else {
                out += Character.toString((char) (0xC0 | ((c >> 6) & 0x1F)));
                out += Character.toString((char) (0x80 | ((c >> 0) & 0x3F)));
            }
        }
        return out;
    }

    private int charCodeAt(String s, int i){
        return Character.codePointAt(s,i);//Integer.parseInt(String.format("\\u%04x", (int) s.charAt(i)));//Character.toString(s.charAt(i)));
    }

    private String utf8to16(String str) {
        String out;
        int i, len,c,char2, char3;

        out = "";
        len = str.length();
        i = 0;
        while (i < len) {
            c = charCodeAt(str,i++);
            switch (c >> 4) {
                case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
                    // 0xxxxxxx
                    out += str.charAt(i - 1);
                    break;
                case 12: case 13:
                    // 110x xxxx   10xx xxxx
                    char2 = charCodeAt(str,i++);
                    out += Character.toString((char) (((c & 0x1f) << 6) | (char2 & 0x3f)));
                    break;
                case 14:
                    // 1110 xxxx  10xx xxxx  10xx xxxx
                    char2 = charCodeAt(str,i++);
                    char3 = charCodeAt(str,i++);
                    out += Character.toString((char) (((c & 0x0f) << 12) |
                            ((char2 & 0x3f) << 6) |
                            ((char3 & 0x3f) << 0)));
                    break;
            }
        }

        return out;
    }


    private String base64encode(String str) {
        String out;
        int i, len, c1, c2, c3;

        len = str.length();
        i = 0;
        out = "";
        while (i < len) {
            c1 = charCodeAt(str,i++) & 0xff;
            if (i == len) {
                out += base64EncodeChars.charAt(c1 >> 2);
                out += base64EncodeChars.charAt((c1 & 0x3) << 4);
                out += "==";
                break;
            }
            c2 = charCodeAt(str,i++);
            if (i == len) {
                out += base64EncodeChars.charAt(c1 >> 2);
                out += base64EncodeChars.charAt(((c1 & 0x3) << 4) | ((c2 & 0xF0) >> 4));
                out += base64EncodeChars.charAt((c2 & 0xF) << 2);
                out += "=";
                break;
            }
            c3 = charCodeAt(str,i++);
            out += base64EncodeChars.charAt(c1 >> 2);
            out += base64EncodeChars.charAt(((c1 & 0x3) << 4) | ((c2 & 0xF0) >> 4));
            out += base64EncodeChars.charAt(((c2 & 0xF) << 2) | ((c3 & 0xC0) >> 6));
            out += base64EncodeChars.charAt(c3 & 0x3F);
        }
        return out;
    }

    private String base64decode(String str) {
        int c1, c2, c3, c4, i, len;
        String out;

        len = str.length();

        i = 0;
        out = "";
        while (i < len) {

            do {
                c1 = base64DecodeChars[charCodeAt(str,i++) & 0xff];
            } while (i < len && c1 == -1);
            if (c1 == -1)
                break;


            do {
                c2 = base64DecodeChars[charCodeAt(str,i++) & 0xff];
            } while (i < len && c2 == -1);
            if (c2 == -1)
                break;

            out += Character.toString((char) ((c1 << 2) | ((c2 & 0x30) >> 4)));


            do {
                c3 = charCodeAt(str,i++) & 0xff;
                if (c3 == 61)
                    return out;
                c3 = base64DecodeChars[c3];
            } while (i < len && c3 == -1);
            if (c3 == -1)
                break;

            out += Character.toString((char) (((c2 & 0xf) << 4) | ((c3 & 0x3c) >> 2)));


            do {
                c4 = charCodeAt(str,i++) & 0xff;
                if (c4 == 61)
                    return out;
                c4 = base64DecodeChars[c4];
            } while (i < len && c4 == -1);
            if (c4 == -1)
                break;
            out += Character.toString((char) (((c3 & 0x03) << 6) | c4));
        }
        return out;
    }

    private String str_decode(String str) {
        return utf8to16(base64decode(str));
    }

    public String str_encode(String str) {
        return base64encode(utf16to8(str));
    }


}
