package com.storn.freechat.me.presenter

import android.Manifest
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Message
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.text.TextUtils
import com.common.common.Constants
import com.common.util.*
import com.jude.beam.expansion.BeamBasePresenter
import com.storn.freechat.R
import com.storn.freechat.login.presenter.LoginContract
import com.storn.freechat.main.ui.HomeActivity
import com.storn.freechat.manager.UserManager
import com.storn.freechat.me.ui.ModifyInfoAct
import com.storn.freechat.me.ui.ProfileDetailAct
import com.storn.freechat.util.DBHelper
import com.storn.freechat.vo.UserVo
import kotlinx.android.synthetic.main.profile_detail_layout.*
import me.wangyuwei.flipshare.FlipShareView
import me.wangyuwei.flipshare.ShareItem
import java.io.File
import java.io.IOException


/**
 * 个人信息控制器
 * Created by tianshutong on 2017/6/27.
 */
class ProfilePresenter : BeamBasePresenter<ProfileDetailAct>(), ProfileContract.Presenter,
        FlipShareView.OnFlipClickListener, LoginContract.ILoginListener {

    val REQUEST_CODE_EDIT = 100
    val REQUEST_CODE_CAMERA = 200
    val REQUEST_CODE_ALBUM = 201
    val REQUEST_CODE_CROP = 202
    val CROP_SIZE = 320
    var mPhotoFile: File? = null
    var mCropFileUri: Uri? = null
    var dialogItems = arrayListOf<ShareItem>()

    override fun onCreateView(view: ProfileDetailAct) {
        super.onCreateView(view)
        view.initToolbar()
        val itemCamera = ShareItem(view.getString(R.string.camera),
                view.resources.getColor(R.color.color_w),
                view.resources.getColor(R.color.color_th),
                BitmapFactory.decodeResource(view.resources, R.mipmap.ic_camera))
        val itemAlbum = ShareItem(view.getString(R.string.album),
                view.resources.getColor(R.color.color_w),
                view.resources.getColor(R.color.colorAccent),
                BitmapFactory.decodeResource(view.resources, R.mipmap.ic_album))
        val itemCancel = ShareItem(view.getString(R.string.cancel),
                view.resources.getColor(R.color.color_w),
                view.resources.getColor(R.color.Blue),
                BitmapFactory.decodeResource(view.resources, R.mipmap.ic_cancel))
        dialogItems.add(0, itemCamera)
        dialogItems.add(1, itemAlbum)
        dialogItems.add(2, itemCancel)
    }

    override fun getProfileData(): UserVo {
        return DBHelper.getInstance().queryProfileInfo(view, PreferenceTool.getString(Constants.LOGIN_JID))
    }

    override fun showCameraDialog() {
        val dialog = FlipShareView.Builder(view, view.headView)
                .addItems(dialogItems)
                .setBackgroundColor(0x60000000)
                .setItemDuration(Constants.ANIM_500)
                .setSeparateLineColor(0x30000000)
                .setAnimType(FlipShareView.TYPE_SLIDE)
                .create()
        dialog.setOnFlipClickListener(this)
    }

    override fun goCamera() {
        if (!PermissionUtil.isReadCamera(view)) {
            ActivityCompat.requestPermissions(view, arrayOf(Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS), REQUEST_CODE_CAMERA)
            return
        }
        startCameraActivity()
    }

    fun startCameraActivity() {
        mPhotoFile = FileUtil.generateCameraFile()
        if (mPhotoFile!!.exists()) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(view,
                    view.packageName + ".com.storn.freechat.fileProvider", mPhotoFile))
            view.startActivityForResult(intent, REQUEST_CODE_CAMERA)
        }
    }

    override fun goAlbum() {
        if (!PermissionUtil.isReadStorage(view)) {
            ActivityCompat.requestPermissions(view, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS), REQUEST_CODE_ALBUM)
            return
        }
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        view.startActivityForResult(intent, REQUEST_CODE_ALBUM)
    }

    override fun goEdit(info: String, tip: String) {
        val intent = Intent(view, ModifyInfoAct::class.java)
        intent.putExtra(Constants.PROFILE_INFO, info)
        intent.putExtra(Constants.PROFILE_TEXT, tip)
        view.startActivityForResult(intent, REQUEST_CODE_EDIT)
    }

    /**
     * 处理拍照结果
     */
    fun doCameraResult() {
        if (mPhotoFile!!.exists()) {
            scanPhoto(mPhotoFile)
        }
    }

    /**
     * 发送广播扫描图片
     */
    fun scanPhoto(file: File?) {
        val uri = FileProvider.getUriForFile(view,
                view.packageName + ".com.storn.freechat.fileProvider", file)
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = uri
        view.sendBroadcast(intent)
        startCropActivity(mPhotoFile, CROP_SIZE)
    }

    /**
     * 处理相册选择结果
     */
    fun doAlbumCropResult(data: Intent) {
        val uri = data.data
        if (uri != null) {
            mPhotoFile = getFileForAlbum(uri)
            startCropActivity(mPhotoFile, CROP_SIZE)
        }
    }

    /**
     * 裁剪后处理结果
     */
    fun doCropResult() {
        if (mCropFileUri != null) {
            mPhotoFile = File(mCropFileUri!!.getPath())
            if (mPhotoFile != null && mPhotoFile!!.exists()) {
                UserManager.getInstance().saveAvatar(view,
                        BitmapFactory.decodeFile(mPhotoFile!!.absolutePath), this)
            }
        } else if (mPhotoFile != null && mPhotoFile!!.exists()) {
            UserManager.getInstance().saveAvatar(view,
                    BitmapFactory.decodeFile(mPhotoFile!!.absolutePath), this)
        } else {
            view.error()
            CommonUtil.showToast(view, "上传头像失败，请重新上传")
        }
    }

    override fun start() {
        view.showLoadingView()
    }

    override fun success() {
        view.mHandler.postDelayed({
            view.success()
            view.updateHeadView(mPhotoFile!!.absolutePath)
            notifyHomeActivity(mPhotoFile!!.absolutePath)
            saveImgUrl(mPhotoFile!!.absolutePath)
        }, Constants.DELAY_2000.toLong())
    }

    override fun fail(error: String) {
        view.error()
    }

    /**
     * 通知home更改
     */
    fun notifyHomeActivity(url: String) {
        if (HomeActivity.homeHandler != null) {
            val message = Message.obtain()
            message.what = Constants.UPDATE_HEADVIEW
            message.obj = url
            HomeActivity.homeHandler?.sendMessage(message)
        }
    }

    /**
     * 保存头像url
     */
    fun saveImgUrl(url: String) {
        val userVo = DBHelper.getInstance().queryProfileInfo(view, PreferenceTool.getString(Constants.LOGIN_JID))
        userVo.img = url
        DBHelper.getInstance().insertOrUpdateAccount(view, userVo)
    }

    /**
     * 从相册获取图片文件
     */
    private fun getFileForAlbum(selectedImage: Uri): File? {
        var cursor: Cursor? = null
        val project = arrayOf(MediaStore.Images.Media.DATA)

        try {
            cursor = view.contentResolver.query(selectedImage, project, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()

                val columnIndex = cursor.getColumnIndex(project[0])
                val picturePath = cursor.getString(columnIndex)
                if (!TextUtils.isEmpty(picturePath)) {
                    return File(picturePath)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
        return null
    }

    /**
     * 处理剪裁结果

     * @param uri  Uri
     * *
     * @param size int
     */
    private fun startCropActivity(imgFile: File?, size: Int) {
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(FileUtil.getImageContentUri(view, imgFile), "image/*")
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true")

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size)
        intent.putExtra("outputY", size)
        intent.putExtra("scale", true)
        intent.putExtra("return-data", false)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())

        // 脸部识别
        intent.putExtra("noFaceDetection", true)

        val fileName = RandomStringUtils.randomAlphanumeric(16) + ".jpg"
        val tempFile = FileUtil.getAppTempFile(fileName)
        try {
            val dir = tempFile.getParentFile()
            if (!dir.exists()) {
                dir.mkdirs()
            }
            if (tempFile.exists()) {
                tempFile.deleteOnExit()
            }
            tempFile.createNewFile()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile))
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        view.startActivityForResult(intent, REQUEST_CODE_CROP)
    }

    override fun dismiss() {
    }

    override fun onItemClick(position: Int) {
        when (position) {
            0 -> goCamera()
            1 -> goAlbum()
            else -> {
            }
        }
    }
}