package com.ethical_techniques.notemaker.utils;

import com.google.firebase.storage.StorageReference;

public class CloudStorageUtil {
    public static final String IMAGES_FOLDER = "images";
    private static StorageReference rootStorageReference;

    private CloudStorageUtil() {
    }

//    private static StorageReference getFolderStorageRef(final String childFolder) {
//        return rootStorageReference.child(childFolder);
//
//    }
//
//    private static StorageReference getFileReference(final String fileName, final StorageReference folderReference) {
//        return folderReference.child(fileName);
//
//    }

    public static void setRootStorageReference(StorageReference rootStorageReference) {
        CloudStorageUtil.rootStorageReference = rootStorageReference;
    }

    public static StorageReference getImageFolderRef() {
        return rootStorageReference.child(IMAGES_FOLDER);
    }


}
