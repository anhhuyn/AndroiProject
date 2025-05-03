package vn.iotstar.ecoveggieapp.helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    /**
     * Sao chép nội dung từ Uri vào một file tạm và trả về đường dẫn File
     */
    public static File getFileFromUri(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        String fileName = getFileName(context, uri);
        if (fileName == null) {
            fileName = "temp_file";
        }

        File cacheDir = context.getCacheDir();
        File tempFile = new File(cacheDir, fileName);

        try (InputStream inputStream = contentResolver.openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            outputStream.flush();
            return tempFile;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy tên file từ Uri
     */
    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = context.getContentResolver()
                    .query(uri, null, null, null, null)) {

                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }

        if (result == null) {
            result = uri.getLastPathSegment();
        }

        return result;
    }
}
