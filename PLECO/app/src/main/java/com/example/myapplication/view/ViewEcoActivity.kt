package com.example.myapplication.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.ViewEcoAdapter
import com.example.myapplication.utils.Photo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlinx.android.synthetic.main.activity_view_eco.*
import java.lang.reflect.Type


class ViewEcoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var photoData: ArrayList<Photo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        setContentView(R.layout.activity_view_eco)
        checkPermission()

        photoData = ReadPhotosData()!!

        Log.d("이미지 sp in Uploaded", photoData.toString())
        for (photo in ReadPhotosData()) {
            Log.d("이미지데이터 in Uploaded", photo?.uri + " : " + photo?.eco_id + "\n")
        }

        // 레이아웃 메니저
        // viewManager = LinearLayoutManager(this)
        viewManager = GridLayoutManager(this, 2)
        viewAdapter = ViewEcoAdapter(photoData)

        recyclerView = findViewById<RecyclerView>(R.id.rv_mission).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        //Intent
        var intentToUploadAct = Intent(this, UploadEcoActivity::class.java)
        var intentToRecommendFoodActivity = Intent(this, UploadedEcoActivity::class.java)


        EcoGallery.setOnClickListener {
            var ecogallery_intent: Intent = Intent(this, ViewEcoActivity::class.java)
            startActivity(ecogallery_intent)
            finish()

            overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        }
        EcoGallery_Camera.setOnClickListener {
            var ecogallery22camera_intent: Intent = Intent(this, UploadEcoActivity::class.java)
            startActivity(ecogallery22camera_intent)
            finish()

            overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        }
        EcoGallery_Growup.setOnClickListener {
            var ecogallery22camera_intent: Intent = Intent(this, GrowUpPleeActivity::class.java)
            startActivity(ecogallery22camera_intent)
            finish()

            overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        }
        EcoGallery_Userinfo.setOnClickListener {
            var ecogallery22userinfo_intent: Intent = Intent(this, UserInfoActivity::class.java)
            startActivity(ecogallery22userinfo_intent)
            finish()

            overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        }


    }

    override fun onResume() {
        super.onResume()
        SavePhotoData(photoData)
    }

    override fun onDestroy() {
        super.onDestroy()
        SavePhotoData(photoData)
    }

    override fun onPause() {
        super.onPause()
        SavePhotoData(photoData)
    }

    override fun onStop() {
        super.onStop()
        SavePhotoData(photoData)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this@ViewEcoActivity.finish()

        overridePendingTransition(R.anim.horizon_exit, R.anim.none)
    }

    private fun SavePhotoData(Photos: ArrayList<Photo>) {
        val preferences: SharedPreferences = getSharedPreferences("PHOTO_LIST", Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        val gson = Gson()
        val json = gson.toJson(Photos)
        editor.putString("PHOTO_LIST", json) // json 타입으로 변환한 객체 저장
        editor.commit() // 완료
    }


    private fun ReadPhotosData(): ArrayList<Photo> {
        val sharedPrefs: SharedPreferences = getSharedPreferences("PHOTO_LIST", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPrefs.getString("PHOTO_LIST", "EMPTY")
        val type: Type = object : TypeToken<ArrayList<Photo?>?>() {}.type
        if (json.toString() != "EMPTY")
            return gson.fromJson(json, type) //Array List 반환

        var photos = ArrayList<Photo>()
        return photos
    }

    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE)

    private val multiplePermissionsCode = 100
    private fun checkPermission() {
        var rejectedPermissionList = ArrayList<String>()

        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for(permission in requiredPermissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }

        //거절된 퍼미션이 있다면...
        if(rejectedPermissionList.isNotEmpty()){
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), multiplePermissionsCode)
        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE), 0)
//        }
    }

}