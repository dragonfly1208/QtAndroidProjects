package an.qt.extendsQtWithJava;
import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
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
import java.util.Arrays;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import java.io.File;

import android.provider.DocumentsContract;
import android.provider.MediaStore;


public class ExtendsQtWithJava extends org.qtproject.qt5.android.bindings.QtActivity
{
    private final static int GET_LOCATION_REQUEST_CODE = 1;
    private final static int CAPTURE_IMAGE_REQUEST_CODE = 2;
    private final static int FILE_MANAGER_REQUEST_CODE = 3;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static NotificationManager m_notificationManager;
    private static ExtendsQtWithJava m_instance;
    private static Class m_nativeClass = null;
    private final static String TAG = "extendsQt";
    public LocationManager m_lm = null;
    public String m_imagePath;
    private static Handler m_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
                Log.d(TAG, "handleMessage:" + msg.what);
            switch (msg.what) {
            case 1:
                String qtVersion = ExtendsQtNative.GetQtVersion();
                Log.d(TAG, "qtVersion:" + qtVersion);
                String toastText = String.format("%s,by %s", (String)msg.obj, qtVersion);
                Log.d(TAG, "toastText:" + toastText);
                Toast toast = Toast.makeText(m_instance, toastText, Toast.LENGTH_LONG);
                toast.show();
                break;
            case 2:
                m_instance.doGetLocation();
                break;
            };
        }
    };
    private LocationListener m_locationListener = new LocationListener(){
        public void onLocationChanged(Location location){
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Log.d(TAG, "longitude - " + longitude + " latitude - " + latitude);
            ExtendsQtNative.OnLocationReady(RESULT_OK, longitude, latitude);
            m_lm.removeUpdates(m_locationListener);
        }

        public void onProviderDisabled(String provider){
            Log.d(TAG, "onProviderDisabled - " + provider);
            ExtendsQtNative.OnLocationReady(-1, 0, 0);
        }

        public void onProviderEnabled(String provider){
            Log.d(TAG, "onProviderEnabled - " + provider);
        }
        public void onStatusChanged(String provider, int status, Bundle extras){
            Log.d(TAG, "onStatusChanged - " + provider + " status - " + status);
            if(status == LocationProvider.OUT_OF_SERVICE)
                ExtendsQtNative.OnLocationReady(-1, 0, 0);
        }
    };

    public ExtendsQtWithJava(){
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

    public static int networkState(){
        ConnectivityManager conMan = (ConnectivityManager) m_instance.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMan.getActiveNetworkInfo() == null ? 0 : 1;
    }

    public static void notify(String s){
        if (m_notificationManager == null) {
            m_notificationManager = (NotificationManager)m_instance.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        //Notification n = new Notification(R.drawable.icon, "A Message from Qt", System.currentTimeMillis());
        //n.flags = Notification.FLAG_AUTO_CANCEL;
        Intent i = new Intent(m_instance, ExtendsQtWithJava.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        PendingIntent contentIntent = PendingIntent.getActivity(
            m_instance,
            R.string.app_name,
            i,
            PendingIntent.FLAG_UPDATE_CURRENT);

//        n.setLatestEventInfo(
//            m_instance,
//            "A Message from Qt",
//            s,
//            contentIntent);
        Notification n = new Notification.Builder(m_instance)
                .setContentTitle("A Message from Qt")
                .setContentText(s)
                .setContentIntent(contentIntent)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon)
                .build();
        n.flags = Notification.FLAG_AUTO_CANCEL;

        m_notificationManager.notify(R.string.app_name, n);
    }

    public static void makeToast(String s){
        m_handler.sendMessage(m_handler.obtainMessage(1, s));
    }

    public static void launchActivity(String action){
        m_instance.startActivity(new Intent(action));
    }

    public static void fileManagerActivity(){
        //m_instance.startActivity(new Intent(action));
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);////多选
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

//        Intent it = Intent.createChooser(intent, "choose file");
        m_instance.startActivityForResult(intent,FILE_MANAGER_REQUEST_CODE);
    }

    public static void emitBroadcast(String action, String extraName, int param){
        Intent i = new Intent(action);
        i.putExtra(extraName, param);
        m_instance.sendBroadcast(i);
        Log.d(TAG,
            String.format("broadcast - %s extra name - %s value - %d", action, extraName, param));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//    	super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
        case GET_LOCATION_REQUEST_CODE:
            if(m_instance.m_lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
                doGetLocation();
            }
            break;
        case CAPTURE_IMAGE_REQUEST_CODE:
            ExtendsQtNative.OnImageCaptured(resultCode, m_imagePath);
            break;
        case FILE_MANAGER_REQUEST_CODE:
        {
            //doFileMenageryResult(requestCode,resultCode, data);
            if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
                Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
                Log.d(TAG, "uri:" + uri);
                Log.d(TAG, "uri Scheme:" + uri.getScheme());
                String path = uri.getPath();//= getPath(this, uri);

                String filePath = null;
                Uri _uri = data.getData();
                Log.d("","URI = "+ _uri);
                if (_uri != null && "content".equals(_uri.getScheme())) {
                    Cursor cursor = this.getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
                    cursor.moveToFirst();
                    filePath = cursor.getString(0);
                    cursor.close();
                } else {
                    filePath = _uri.getPath();
                }
                Log.d("-----","Chosen path = "+ filePath);




                if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                        Log.d(TAG, "uri:---1");
                    path = uri.getPath();
                }else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4+
                        Log.d(TAG, "uri:---2");
                    path = getPath(this, uri);
                }
                Log.d(TAG, "fpath:" + path);
                //String[] proj = {MediaStore.Images.Media.DATA};
//                Cursor actualimagecursor = getContentResolver().query(uri, proj, null, null, null);
//                int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                actualimagecursor.moveToFirst();
//                String img_path = actualimagecursor.getString(actual_image_column_index);
//
//



                ExtendsQtNative.onFileManager(resultCode, path);
            }

         }
            break;
        default:
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static void getLocation(){
        Log.d(TAG, "getLocation");
        if(m_instance.m_lm == null)
        m_instance.m_lm = (LocationManager)m_instance.getSystemService(Context.LOCATION_SERVICE);
        if(m_instance.m_lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
            m_instance.m_handler.sendMessage(m_handler.obtainMessage(2));
        }else{
            // open gps
            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            m_instance.startActivityForResult(intent, GET_LOCATION_REQUEST_CODE);
        }
    }

    private void doGetLocation(){
        //LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗

        String provider = m_lm.getBestProvider(criteria, true); // 获取GPS信息
        Log.d(TAG, "provider - " + provider);
        m_lm.requestLocationUpdates(provider, 1000, 1, m_locationListener);
        //ExtendsQtNative.OnLocationReady(0, 0, 0);
        //ExtendsQtNative.OnImageCaptured(0);
        Log.d(TAG, "after call requestLocationUpdates");
        /*
        Location location = m_lm.getLastKnownLocation(provider); // 通过GPS获取位置
        if(location != null){
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Log.d(TAG, "before call native OnLocationReady, longitude - " + longitude + " latitude - " + latitude);
            ExtendsQtNative.OnLocationReady(RESULT_OK, longitude, latitude);
        }else{
            Log.d(TAG, "no location");
        }
        */
   }

   public static String getSdcardPath(){
       File sdDir = null;
       boolean sdCardExist = Environment.getExternalStorageState()
                       .equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
       Log.d(TAG, "sdCardExist:" + sdCardExist);
       if(sdCardExist)
       {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            return sdDir.toString();
       }
       return "";
   }

   public void initCaptureImagePath(){
       if( m_imagePath == null ){
           String sdPath = getSdcardPath();
           Log.d(TAG, "sdPath:" + sdPath);
           if(sdPath.isEmpty()){
               sdPath = "/sdcard";
           }
           m_imagePath = String.format("%s/qtimagecap", sdPath);
           File imageDir = new File(m_imagePath);
           if(!imageDir.exists()){
               imageDir.mkdirs();
           }
           m_imagePath = String.format("%s/qtimagecap/cap.jpg", sdPath);
           Log.d(TAG, "capture to - " + m_imagePath);
       }
   }

   public static void captureImage(){
       m_instance.initCaptureImagePath();
       File imageFile = new File(m_instance.m_imagePath);
       Uri uri = Uri.fromFile(imageFile);
       Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//
       m_instance.startActivityForResult(intent, CAPTURE_IMAGE_REQUEST_CODE);
   }

protected  void doFileMenageryResult(int requestCode, int resultCode, Intent data) {
         if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
             Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
             Log.d(TAG, "uri:" + uri.toString());
//	        String[] proj = {MediaStore.Images.Media.DATA};
//	        Cursor actualimagecursor = getContentResolver().query(uri, proj, null, null, null);
//	        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//	        actualimagecursor.moveToFirst();
//	        String img_path = actualimagecursor.getString(actual_image_column_index);
//	        File file = new File(img_path);
//	        Toast.makeText(MainActivity.this, file.toString(), Toast.LENGTH_SHORT).show();
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

