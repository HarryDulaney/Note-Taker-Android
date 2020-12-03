package com.ethical_techniques.notemaker.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import com.ethical_techniques.notemaker.BuildConfig;

import java.io.File;

public class FileUtil {

    public static final String PHOTO = "photo.user";
    public static final String NOTEFILE = "note.file";
    public static final String AUDIO = "note.audio";
    private static final String FILE_DIRECTORY = "AppDirectory";
    private static final String BLANK = "BlankFile";
    private static final Uri DEFAULT_IMAGE_SAVE_DIR = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final String FILE_AUTHORITY = BuildConfig.APPLICATION_ID + ".fileprovider";

    FileUtil() {
        throw new UnsupportedOperationException("How dare you!, Seriously though please don't do that.");
    }

    public static File getMakeFileByType(final String userId, final String fileType, final Context context) throws Exception {
        Boolean success = false;
        if (fileType.equals(PHOTO)) {

            return new File(context.getFilesDir() + File.separator + getFileName(userId, fileType));

        } else {
            throw new Exception("File Couldn't be found or created...");
        }
    }

    public static String getFileName(final String userId, String fileType) {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "";
        switch (fileType) {
            case PHOTO:
                filename = "IMG_" + userId + ".jpg";
                break;
            case NOTEFILE:
                filename = "FILE_TEXT_" + userId + ".txt";
                break;
            case AUDIO:
                filename = "WAV_" + userId + ".wav";
                break;
        }
        return filename;

    }

    public static String getFileExt(Uri uri, Context context) {
        ContentResolver c = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(uri));
    }

    public static Uri getDefaultImageSaveDir() {
        return DEFAULT_IMAGE_SAVE_DIR;
    }

}
