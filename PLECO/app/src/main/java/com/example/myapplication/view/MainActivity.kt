package com.example.myapplication.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.utils.ImageClassifier
import com.example.myapplication.utils.SetKey.INPUT_SIZE
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileNotFoundException

//ConstrainLayout 정렬 코드: https://recipes4dev.tistory.com/161
class MainActivity : AppCompatActivity() {

    private val CHOOSE_IMAGE = 1001
    private lateinit var photoImage: Bitmap
    private lateinit var classifier: ImageClassifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)
        classifier = ImageClassifier(getAssets())
//        checkPermission()
        imageResult.setOnClickListener {
            choosePicture()
        }

        Main_EcoGallery.setOnClickListener {
            var main2ecogallery_intent: Intent = Intent(this, ViewEcoActivity::class.java)
            startActivity(main2ecogallery_intent)
            finish()

        }
        Main_Camera.setOnClickListener {
            var main2camera_intent: Intent = Intent(this, UploadEcoActivity::class.java)
            startActivity(main2camera_intent)
            finish()

        }
        Main_Growup.setOnClickListener {
            var main2growup_intent: Intent = Intent(this,GrowUpPleeActivity::class.java)
            startActivity(main2growup_intent)
            finish()

        }
        Main_Userinfo.setOnClickListener {
            var main2userinfo_intent: Intent = Intent(this, UserInfoActivity::class.java)
            startActivity(main2userinfo_intent)
            finish()

        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }
    }

    private fun choosePicture() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE) // 갤러리 입장
        startActivityForResult(intent, CHOOSE_IMAGE) // 이미지 선택하여 전달됨
    }

    /*
    Bitmap.createScaleBitemap 은 매우 중요한 단계
    이를 거치지 않을 경우 .ArrayOutOfIndexException 이 발생할 수 있음
    샘플 이미지가 모델 이미지와 같은 크기여야 하기 때문
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_IMAGE && resultCode == Activity.RESULT_OK)
            try {
                val stream = contentResolver!!.openInputStream(data!!.getData()!!)
                if (::photoImage.isInitialized) photoImage.recycle()
                photoImage = BitmapFactory.decodeStream(stream)
                photoImage = Bitmap.createScaledBitmap(photoImage, INPUT_SIZE, INPUT_SIZE, false)
                imageResult.setImageBitmap(photoImage)

                classifier.recognizeImage(photoImage).subscribeBy(
                    onSuccess = {
                        txtResult.text = it.toString()
                    }
                )
                Log.d("트라이", "classifier")

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 0) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                imageResult.setEnabled(true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        classifier.close()
    }
}