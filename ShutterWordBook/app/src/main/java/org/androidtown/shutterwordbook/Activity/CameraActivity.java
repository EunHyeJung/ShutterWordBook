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
    //������ ����� ���, �ܺ� ������� �ֻ��� ���
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
                Toast.makeText(this, "������ ������ ������ �����ϴ�.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
    }

    // ��Ŀ�� �����ϸ� �Կ� �㰡
    AutoFocusCallback mAutoFocus = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            mShutter.setEnabled(success);
            mSurface.mCamera.takePicture(null, null, mPicture);

        }
    };

    // ���� ����.
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
             * Android�� �߰��� File�� MediaPlayer�� Gallery���� �ν����� ���ϴ� ��찡 �߻��Ѵ�.
             * Android�� Media File�� DB�� ���� �ϴµ� File�� �߰��Ǿ����� File format�� �´�
             * DB�� ���� �߰��� ���� �ʾұ� �����̴�.
             * Media File�� DB�� �߰��ϵ��� Android Platform�� �˷��ִ� ����̴�.
             */
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            //����ȭ�� URI�ּҷκ��� Uri��ü�� �ν��Ͻ�ȭ
            uri = Uri.parse("file://" + path);
            intent.setData(uri);
            //�߰��� �̹����� �̵�� ������ DB�� �߰��ϴ� �Լ�
            sendBroadcast(intent);
            Toast.makeText(getApplicationContext(), "������ ���� �Ǿ����ϴ�", Toast.LENGTH_SHORT).show();

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
        } else {  //���� ��� ��� ��ư ������ ��
            onCreate(new Bundle());
        }
    }

    protected void startCrop() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        //_path ���Ͽ��� �ҷ��� outputFileUri�� �ٽ� ������µ�
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("scale", true);
        intent.putExtra("output", uri);
        startActivityForResult(intent, 1);
    }

    //���� ��������
    protected void onPhotoTaken() {
        //�о���̷��� �̹��� ������ �˾Ƴ��� ���� ��ü
        BitmapFactory.Options options = new BitmapFactory.Options();
        //�̹����� �ػ󵵸� ����� 1�� ���� ���� ��Ÿ����. (1/4)
        //���� ���� 1/4 ũ��� �ٿ� �о�帲, ������ 1/16
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(path,  options);

        //���� �ν�
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

    // ǥ�� ������ ī�޶� �����ϰ� �̸����� ����
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

    // ǥ�� �ı��� ī�޶� �ı��Ѵ�.
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    // ǥ���� ũ�Ⱑ ������ �� ������ �̸����� ũ�⸦ ���� �����Ѵ�.
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewSize(width, height);
        params.setPictureSize(width, height);
        params.setRotation(90);
        mCamera.setParameters(params);
        mCamera.startPreview();
    }


}
