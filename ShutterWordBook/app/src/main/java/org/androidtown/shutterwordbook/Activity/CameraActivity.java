package org.androidtown.shutterwordbook.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import com.googlecode.tesseract.android.TessBaseAPI;
import org.androidtown.shutterwordbook.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class CameraActivity extends Activity {
    Button mShutter;
    MyCameraSurface mSurface;
    //사진이 저장될 경로, 외부 저장소의 최상위 경로
    public String mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PICFOLDER;
    public static String path;
    static final String PICFOLDER = "CameraTest";
    Uri uri;
    String TAG = "CameraTest";
    public static final String lang = "eng";
    Bundle extra;
    Intent cameraIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        extra = new Bundle();
        cameraIntent = new Intent();
        mSurface = (MyCameraSurface)findViewById(R.id.previewFrame);
        mShutter = (Button)findViewById(R.id.button1);
        mShutter.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mSurface.mCamera.autoFocus(mAutoFocus);
            }
        });

        File fRoot = new File(mRootPath);
        if (fRoot.exists() == false) {
            if (fRoot.mkdir() == false) {
                Toast.makeText(this, "사진을 저장할 폴더가 없습니다.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
    }

    // 포커싱 성공하면 촬영 허가
    AutoFocusCallback mAutoFocus = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            mShutter.setEnabled(success);
            mSurface.mCamera.takePicture(null, null, mPicture);

        }
    };

    // 사진 저장.
    PictureCallback mPicture = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            Calendar calendar = Calendar.getInstance();
            String FileName = String.format("SH%02d%02d%02d-%02d%02d%02d.jpg",
                    calendar.get(Calendar.YEAR) % 100, calendar.get(Calendar.MONTH)+1,
                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
            path = mRootPath + "/" + FileName;

            File file = new File(path);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.flush();
                fos.close();
            } catch (Exception e) {

                return;
            }

            /*************
             * Android는 추가한 File을 MediaPlayer나 Gallery에서 인식하지 못하는 경우가 발생한다.
             * Android는 Media File을 DB로 관리 하는데 File은 추가되었지만 File format에 맞는
             * DB에 아직 추가가 되지 않았기 때문이다.
             * Media File을 DB에 추가하도록 Android Platform에 알려주는 방법이다.
             */
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            //석식화된 URI주소로부터 Uri객체를 인스턴스화
            uri = Uri.parse("file://" + path);
            intent.setData(uri);
            //추가된 이미지를 미디어 파일의 DB에 추가하는 함수
            sendBroadcast(intent);
            Toast.makeText(getApplicationContext(), "사진이 저장 되었습니다", Toast.LENGTH_SHORT).show();

            startCrop();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "resultCode: " + resultCode);

        if (resultCode == -1) {
            if(requestCode == 1){
                onPhotoTaken();
            }
        } else {  //사진 찍고 취소 버튼 눌렀을 때
            onCreate(new Bundle());
        }
    }

    protected void startCrop() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        //_path 파일에서 불러온 outputFileUri에 다시 덮어씌웠는데
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("scale", true);
        intent.putExtra("output", uri);
        startActivityForResult(intent, 1);
    }

    //사진 가져오기
    protected void onPhotoTaken() {
        //읽어들이려는 이미지 정보를 알아내기 위한 객체
        BitmapFactory.Options options = new BitmapFactory.Options();
        //이미지의 해상도를 몇분의 1로 줄일 지를 나타낸다. (1/4)
        //가로 세로 1/4 크기로 줄여 읽어드림, 면적은 1/16
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(path,  options);

        //글자 인식
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(mRootPath, lang);
        baseApi.setImage(bitmap);

        String recognizedText = baseApi.getUTF8Text();

        baseApi.end();

        // You now have the text in recognizedText var, you can do anything with it.
        // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
        // so that garbage doesn't make it to the display.

        Log.v(TAG, "OCRED TEXT: " + recognizedText);

        if ( lang.equalsIgnoreCase("eng") ) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }
        recognizedText = recognizedText.trim();
        extra.putString("recognizedText", recognizedText);
        cameraIntent.putExtras(extra);
        this.setResult(RESULT_OK, cameraIntent);

        this.finish();
    }

}

class MyCameraSurface extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mHolder;
    Camera mCamera;

    public MyCameraSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    // 표면 생성시 카메라 오픈하고 미리보기 설정
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            mCamera.release();
            mCamera = null;
        }
    }

    // 표면 파괴시 카메라도 파괴한다.
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    // 표면의 크기가 결정될 때 최적의 미리보기 크기를 구해 설정한다.
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewSize(width, height);
        params.setPictureSize(width, height);
        params.setRotation(90);
        mCamera.setParameters(params);
        mCamera.startPreview();
    }


}
