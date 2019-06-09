package com.develop.`in`.come.comeinfrontbase.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.util.Constants
import com.develop.`in`.come.comeinfrontbase.util.GraphicsUtil
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProfileAvatarFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProfileAvatarFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ProfileAvatarFragment : Fragment() {

    lateinit var mSharedPreferences: SharedPreferences
    lateinit var mIvAvatar: ImageView
    lateinit var file: File
    lateinit var uri: Uri
    lateinit var camIntent: Intent
    lateinit var galIntent: Intent
    lateinit var cropIntent: Intent
    lateinit var mIbUploadCamera: LinearLayout
    lateinit var mIbUploadGallery: LinearLayout
    val RequestPermissionCode=1
    val REQUEST_CODE_GALLERY = 0x1
    val REQUEST_CODE_TAKE_PICTURE = 0x2
    val REQUEST_CODE_CROP_IMAGE = 0x3
    val MEDIA_TYPE_JPG = MediaType.parse("multipart/form-data");



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    fun initViews(v:View){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        //image begin
        mIvAvatar = v.findViewById(R.id.iv_profile_avatar) as ImageView
        val graphicsUtil = GraphicsUtil()
        val avatar = graphicsUtil.decodeBase64(mSharedPreferences.getString("Avatar",""))
        if (avatar!= null)
            mIvAvatar.setImageBitmap(graphicsUtil.getRoundedShape(avatar,300,300))

        var permissionCheck = ContextCompat.checkSelfPermission(context!!,android.Manifest.permission.CAMERA)
        if(permissionCheck == PackageManager.PERMISSION_DENIED)
            RequestRuntimePermission()
        val state = Environment.getExternalStorageState()
        //image end

    }

    fun initButtons(v:View) {
        mIbUploadCamera = v.findViewById<View>(R.id.ib_upload_avatar_camera) as LinearLayout
        mIbUploadGallery = v.findViewById<View>(R.id.ib_upload_avatar_gallery) as LinearLayout

        mIbUploadCamera.setOnClickListener {
            CameraOpen()
        }
        mIbUploadGallery.setOnClickListener {
            GalleryOpen()
        }
    }

    private fun RequestRuntimePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity!!,android.Manifest.permission.CAMERA))
            Toast.makeText(context,"Allow Camera Permission", Toast.LENGTH_SHORT).show()
        else
        {
            ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.CAMERA),RequestPermissionCode)
        }
    }
    private fun showSnackBarMessage(message: String) {
        Snackbar.make(view!!.findViewById(R.id.activity_profile_llayout), message, Snackbar.LENGTH_SHORT).show()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile_avatar, container, false)
        initViews(view)
        initButtons(view)
        return view
    }

    private fun GalleryOpen() {
        galIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(Intent.createChooser(galIntent,"Select Image from Gallery"),2)
    }

    private fun CameraOpen() {
        camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        file = File(
            Environment.getExternalStorageDirectory(),
            "file" + System.currentTimeMillis().toString() + ".jpg"
        )
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(context!!, "com.example.file.provider", file)
            camIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri)
        } else {
            camIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        }

        camIntent.putExtra("return-data", true)
        startActivityForResult(camIntent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 0 && resultCode == Activity.RESULT_OK)
            CropImage()
        else if(requestCode == 2){
            if(data != null){
                uri = data.data!!
                CropImage()
            }
        } else if(requestCode == 1){
            val token = mSharedPreferences.getString(Constants.TOKEN,"")
            println("Debug: This is token $token")
            val client = OkHttpClient()
            var requestBody = MultipartBody.Builder().setType(MEDIA_TYPE_JPG)
                .addFormDataPart("file", "myFileName", RequestBody.create(MediaType.parse("video/quicktime"), file))
                .build() as RequestBody
            val request = Request.Builder()
                .url(Constants.BASE_URL + "media/setImage")
                .addHeader("Authorization", "Bearer $token")
                .put(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    val mMessage = e.message.toString()
                    println(mMessage)
                    handleError(mMessage);
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: okhttp3.Response) {

                    val mMessage = response.body().string()
                    println(mMessage)
                    if (response.isSuccessful()){
                        try {
                            //handleResponse(mMessage)
                            println(response)
                            val jsonResponse = JSONObject(mMessage).getJSONObject("data")
                            var url = jsonResponse.getString("file")
                            println(url)

                        } catch (e: Exception){
                            e.printStackTrace();
                        }
                    } else {
                        handleError(response.toString())
                    }
                }
            })

            var bundle = data!!.getExtras();
            var bitmap = bundle.getParcelable("data") as Bitmap
            //  var fos = FileOutputStream(file);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            // fos.close();
            val graphicUtil = GraphicsUtil();
            var croppedImage = graphicUtil.getRoundedShape(bitmap,300,300);
            mIvAvatar.setImageBitmap(croppedImage);
            val editor = mSharedPreferences.edit()
            editor.putString("Avatar", graphicUtil.encodeTobase64(bitmap))
            editor.apply()
        }
    }

    private fun handleError(error: String) {
        //   val json = JSONObject(error)
        println(error)
        showSnackBarMessage("Error: " + error)
    }

    private fun CropImage() {
        try{
            cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(uri,"image/*")
            cropIntent.putExtra("crop","true")
            cropIntent.putExtra("aspectX", 1)
            cropIntent.putExtra("aspectY", 1)
            activity!!.intent.putExtra("outputX", 1024); // intent?
            activity!!.intent.putExtra("outputY", 1024); // intent?
            cropIntent.putExtra("return-data",true)

            startActivityForResult(cropIntent,1)
        } catch (e: ActivityNotFoundException){
            println(e)
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            RequestPermissionCode -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(context,"Permission granted", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(context,"Permission canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
