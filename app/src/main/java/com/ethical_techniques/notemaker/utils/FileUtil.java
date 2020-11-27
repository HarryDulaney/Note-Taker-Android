package com.ethical_techniques.notemaker.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    public static final String PHOTO = "photo.user";
    public static final String NOTEFILE = "note.file";
    public static final String AUDIO = "note.audio";
    private static final String FILE_DIRECTORY = "AppDirectory";
    private static final String BLANK = "BlankFile";

    private FileUtil() {
    }

    public static File getMakeFileByType(final String fileType, final Context context) throws Exception {
        final File fileDir = context.getDir(FILE_DIRECTORY, Context.MODE_PRIVATE);

        if (fileType.equals(PHOTO)) {
            return new File(fileDir, getFileName(fileType));
        }

        File dummyFile = context.getFilesDir();

        if (!dummyFile.exists()) {
            if (dummyFile.mkdirs()) {
                dummyFile = new File(dummyFile, getFileName(fileType));
            }
            try {
                if (dummyFile.createNewFile()) {
                    return dummyFile;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(FileUtil.class.getName(), "IOException occurred trying to create the empty file@ : "
                        + dummyFile.getAbsolutePath());
            }
        }
        throw new Exception("Couldn't create a file, check that you have the correct access rights to " +
                "read and write to the app directory.");

    }

    public static String getFileName(final String fileType) {
        String filename = "";
        switch (fileType) {
            case PHOTO:
                filename = "IMG_" + "user_image" + ".jpg";
                break;
            case NOTEFILE:
                filename = "FILE_TEXT_" + "user_image" + ".txt";
                break;
            case AUDIO:
                filename = "WAV_" + "user_audio" + ".wav";
                break;
        }
        return filename;

    }


}
