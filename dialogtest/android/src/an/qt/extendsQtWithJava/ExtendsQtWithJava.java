package an.qt.extendsQtWithJava;
import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.app.PendingIntent;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.location.LocationManager;
import android.location.Criteria;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import java.lang.ClassLoader;
import dalvik.system.DexClassLoader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import java.io.File;

import android.provider.DocumentsContract;
import android.provider.MediaStore;


public class ExtendsQtWithJava extends org.qtproject.qt5.android.bindings.QtActivity
{
    //private final static int GET_LOCATION_REQUEST_CODE = 1;
    //private final static int CAPTURE_IMAGE_REQUEST_CODE = 2;
    private final static int FILE_MANAGER_REQUEST_CODE = 3;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
   // private static NotificationManager m_notificationManager;
    private static ExtendsQtWithJava m_instance;
    private static Class m_nativeClass = null;
    private final static String TAG = "extendsQt";
    public LocationManager m_lm = null;

    public ExtendsQtWithJava(){
        Log.d(TAG, "ExtendsQtWithJava constructor");
        m_instance = this;
    }

    /**
     * 获得类的成员变量值，包括私有成员
     * @param instance 被调用的类
     * @param variableName 成员变量名
     *
     */
    public static Object get(Object instance, String variableName)
    {
        Class targetClass = instance.getClass().getSuperclass();
        org.qtproject.qt5.android.bindings.QtActivity superInst = (org.qtproject.qt5.android.bindings.QtActivity)targetClass.cast(instance);
        Log.d(TAG, "super class name - " + targetClass.getName() + " super instance -" + superInst);
        Field field;
        try {
            field = targetClass.getDeclaredField(variableName);
            Log.d(TAG, "field name - " + field.getName());
            field.setAccessible(true);//访问私有必须调用
            return field.get(superInst);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void loadExtendsQtNative(){
    if(m_nativeClass == null){
        DexClassLoader loader = (DexClassLoader)get(m_instance, "m_classLoader");
        Log.d(TAG, "loader - " + loader);
        if(loader != null){
            try{
                m_nativeClass = loader.loadClass("an.qt.extendsQtWithJava.ExtendsQtNative");
                Log.d(TAG, "load ExtendsQtNative OK!");
            }catch(ClassNotFoundException e){
                Log.d(TAG, "load ExtendsQtNative failed");
            }
        }
    }
    }

    protected  List<Uri> getUris(Intent data){
        List<Uri> uris = new ArrayList<Uri>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ClipData clipData = data.getClipData();
            if (null != clipData) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    uris.add(item.getUri());
                }
            } else {
                uris.add(data.getData());
            }
        } else {
                uris.add(data.getData());
        }
        return uris;
    }


    public static void fileManagerActivity(boolean multi){
        Log.d(TAG, "fileManagerActivity1");
        //m_instance.startActivity(new Intent(action));
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multi == true);//多选
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//        intent.putExtra("pointer", 12345678);//无法用putExtra 传递数据在 onActivityResult 中接收,放在下面的it中也不行
//        Bundle b=new Bundle();
//        b.putString("listenB", "zp");
//        intent.putExtras(b);

        //Intent it = Intent.createChooser(intent, "choose file");
        m_instance.startActivityForResult(intent,FILE_MANAGER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:"+requestCode+";"+resultCode);
        switch(requestCode){
        case FILE_MANAGER_REQUEST_CODE:
        {
                String arr[] = {};
            if (resultCode == Activity.RESULT_OK) {

//            data.getIntExtra("pointer", 0);//无法接收数据
//            Bundle b=data.getExtras();

            //doFileMenageryResult(requestCode,resultCode, data);
/*            if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
                Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
                Log.d(TAG, "uri:" + uri);
                Log.d(TAG, "uri Scheme:" + uri.getScheme());
                String path = uri.getPath();//= getPath(this, uri);

                if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                        Log.d(TAG, "uri:---1");
                    path = uri.getPath();
                }else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4+
                        Log.d(TAG, "uri:---2");
                    path = getPath(this, uri);
                }
                Log.d(TAG, "fpath:" + path);


                ExtendsQtNative.onFileManager(resultCode, path);

                */
                List<Uri> uris = getUris(data);
               List<String> paths = new ArrayList<String>();
               for(Uri uri : uris){
                       String path = "";
                   if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                       Log.d(TAG, "uri:---1");
                           path = uri.getPath();
                       }else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4+
                           Log.d(TAG, "uri:---2");
                           path = getPath(this, uri);
                       }
                                Log.d(TAG, "fpath:" + path);
                   if(path!=null && !path.isEmpty()){
                       paths.add(path);
                   }
               }


               Log.d(TAG, "fpaths:" + paths.toString());

               arr = paths.toArray(new String[paths.size()]);
               Log.d(TAG, "arr:" + arr.toString());

               ExtendsQtNative.onFileManager(resultCode, arr);
            }else if(resultCode == Activity.RESULT_CANCELED ){
                //String arr[] = {};
                ExtendsQtNative.onFileManager(resultCode,  arr);
            }
        }

            break;
        default:
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

/**
 * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
 */
public String getPath(final Context context, final Uri uri) {
        Log.d(TAG, "getPath:---1");
    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    Log.d(TAG, "getPath:---2-getAuthority:"+uri.getAuthority());
    // DocumentProvider
    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        Log.d(TAG, "getPath:---3");
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            if ("primary".equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            }
        }
        // DownloadsProvider
        else if (isDownloadsDocument(uri)) {

            final String id = DocumentsContract.getDocumentId(uri);
            final Uri contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

            return getDataColumn(context, contentUri, null, null);
        }
        // MediaProvider
        else if (isMediaDocument(uri)) {
                Log.d(TAG, "getPath:---4");
            final String docId = DocumentsContract.getDocumentId(uri);
            Log.d(TAG, "docId:"+docId);
            final String[] split = docId.split(":");
            final String type = split[0];
            Log.d(TAG, "docId split:"+Arrays.toString(split));
            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{split[1]};
            Log.d(TAG, "contentUri:"+contentUri);

            return getDataColumn(context, contentUri, selection, selectionArgs);
        }
    }
    // MediaStore (and general)
    else if ("content".equalsIgnoreCase(uri.getScheme())) {
        return getDataColumn(context, uri, null, null);
    }
    // File
    else if ("file".equalsIgnoreCase(uri.getScheme())) {
        return uri.getPath();
    }
    return null;
}

/**
 * Get the value of the data column for this Uri. This is useful for
 * MediaStore Uris, and other file-based ContentProviders.
 *
 * @param context       The context.
 * @param uri           The Uri to query.
 * @param selection     (Optional) Filter used in the query.
 * @param selectionArgs (Optional) Selection arguments used in the query.
 * @return The value of the _data column, which is typically a file path.
 */
public String getDataColumn(Context context, Uri uri, String selection,
                            String[] selectionArgs) {
        Log.d(TAG, "getDataColumn:"+uri+";"+selection+";"+Arrays.toString(selectionArgs));
//content://media/external/images/media;  _id=?;[715746]
    Cursor cursor = null;
    final String column = "_data";
    final String[] projection = {column};

    try {
/**********权限校验需要 >=sdk23 版本*****************************************/
        int hasWriteContactsPermisson = ContextCompat.checkSelfPermission(context,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.d(TAG, "hasWriteContactsPermisson:"+hasWriteContactsPermisson);
        if(hasWriteContactsPermisson !=
            PackageManager.PERMISSION_GRANTED)
        {
                ActivityCompat.requestPermissions(m_instance,new String[]
                {Manifest.permission.WRITE_CONTACTS},
                REQUEST_CODE_ASK_PERMISSIONS);

            return "";
        }
/********************************/
        Log.d(TAG, "getDataColumn--1");
        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                null);
        Log.d(TAG, "getDataColumn--2");
        if (cursor != null && cursor.moveToFirst()) {
                Log.d(TAG, "getDataColumn--3");
            final int column_index = cursor.getColumnIndexOrThrow(column);
            Log.d(TAG, "getDataColumn--4");
            return cursor.getString(column_index);
        }
    } catch(Exception e){
        Log.d(TAG, "getDataColumn err:"+e.getMessage());
    }finally {
        if (cursor != null)
            cursor.close();
    }
    return "null";
}


/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
public boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 */
public boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 */
public boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
}


}

